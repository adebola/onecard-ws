package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.AutoRechargeMapper;
import io.factorialsystems.msscprovider.domain.query.SearchByDate;
import io.factorialsystems.msscprovider.domain.query.SearchByString;
import io.factorialsystems.msscprovider.domain.rechargerequest.*;
import io.factorialsystems.msscprovider.dto.PagedDto;
import io.factorialsystems.msscprovider.dto.recharge.*;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import io.factorialsystems.msscprovider.mapper.recharge.AutoRechargeMapstructMapper;
import io.factorialsystems.msscprovider.service.bulkrecharge.NewBulkRechargeService;
import io.factorialsystems.msscprovider.service.file.ExcelReader;
import io.factorialsystems.msscprovider.service.file.FileUploader;
import io.factorialsystems.msscprovider.service.file.UploadFile;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoRechargeService {
    private final FileUploader fileUploader;
    private final AutoRechargeMapper autoRechargeMapper;
    private final NewBulkRechargeService newBulkRechargeService;
    private final AutoRechargeMapstructMapper autoRechargeMapstructMapper;

    public static final int AUTO_RECURRING_WEEKLY_TYPE = 1;
    public static final int AUTO_RECURRING_MONTHLY_TYPE = 2;

    private static final String[] days = {
            "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"
    };
    private static final String[] months = {
            "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    };

    public AutoRechargeResponseDto uploadRecharge(AutoUploadFileRechargeRequestDto dto, MultipartFile file) {
        UploadFile uploadFile = fileUploader.uploadFile(file);
        ExcelReader excelReader = new ExcelReader(uploadFile);
        List<IndividualRequestDto> individualRequests = excelReader.readContents();
        AutoRechargeRequestDto autoRechargeRequestDto = autoRechargeMapstructMapper.uploadToRechargeRequestDto(dto);
        autoRechargeRequestDto.setRecipients(autoRechargeMapstructMapper.listAutoToNonAuto(individualRequests));
        return saveService(autoRechargeRequestDto);
    }

    @Transactional
    public AutoRechargeResponseDto saveService(AutoRechargeRequestDto dto) {

        AutoRechargeRequest request = autoRechargeMapstructMapper.dtoToRequest(dto);

        final String id = UUID.randomUUID().toString();
        request.setId(id);

        List<Integer> daysOfPeriod = null;

        if (dto.getDaysOfWeek() != null && !dto.getDaysOfWeek().isEmpty()) {
            request.setRecurringType(AUTO_RECURRING_WEEKLY_TYPE);
            daysOfPeriod = dto.getDaysOfWeek();
        } else if (dto.getDaysOfMonth() != null && !dto.getDaysOfMonth().isEmpty()) {
            request.setRecurringType(AUTO_RECURRING_MONTHLY_TYPE);
            daysOfPeriod = dto.getDaysOfMonth();
        } else {
            throw new RuntimeException("Nothing to AutoSchedule Days of Week or Days of Month must be specified");
        }

        autoRechargeMapper.saveAutoRecharge(request);

        List<AutoRecurringEvent> events = daysOfPeriod.stream()
                .distinct()
                .map(d -> AutoRecurringEvent.builder().autoRequestId(id).dayOfPeriod(d).build())
                .collect(Collectors.toList());
        autoRechargeMapper.saveAutoRecurringEvents(events);

        request.getRecipients().forEach(recipient -> recipient.setAutoRequestId(id));

        autoRechargeMapper.saveRecipients(request.getRecipients());

        log.info(String.format("AutoRecharge (%s) Saved Successfully by (%s)", id, ProviderSecurity.getUserName()));

        return AutoRechargeResponseDto.builder()
                .id(id)
                .message(String.format("Auto Recharge Submitted reference is %s", request.getId()))
                .paymentMode("wallet")
                .build();
    }

    @Transactional
    public void updateService(String id, AutoRechargeRequestDto dto) {
        AutoRechargeRequest request = autoRechargeMapstructMapper.dtoToRequest(dto);
        request.setId(id);

        List<Integer> daysOfPeriod = null;

        if (dto.getDaysOfWeek() != null && !dto.getDaysOfWeek().isEmpty()) {
            request.setRecurringType(AUTO_RECURRING_WEEKLY_TYPE);
            daysOfPeriod = dto.getDaysOfWeek();
        } else if (dto.getDaysOfMonth() != null && !dto.getDaysOfMonth().isEmpty()) {
            request.setRecurringType(AUTO_RECURRING_MONTHLY_TYPE);
            daysOfPeriod = dto.getDaysOfMonth();
        } else {
            throw new RuntimeException("Nothing to AutoSchedule Days of Week or Days of Month must be specified");
        }

        autoRechargeMapper.updateAutoRecharge(request);

        List<AutoRecurringEvent> currentEvents = autoRechargeMapper.disableAndLoadRecurringEventsByAutoId(id);

        List<Integer> enableQueue = new ArrayList<>();
        List<Integer> createQueue = new ArrayList<>();

        daysOfPeriod.forEach(day -> {
            Optional<AutoRecurringEvent> optionalEvent = currentEvents.stream().filter(e -> Objects.equals(e.getDayOfPeriod(), day)).findFirst();

            if (optionalEvent.isPresent()) {
                AutoRecurringEvent event = optionalEvent.get();

                if (event.isDisabled()) {
                    enableQueue.add(event.getId());
                }
            } else {
                createQueue.add(day);
            }
        });

        if (!enableQueue.isEmpty()) {
            List<AutoRecurringEvent> updateEvents = enableQueue.stream()
                    .distinct()
                    .map(d -> AutoRecurringEvent.builder().id(d).disabled(false).build())
                    .collect(Collectors.toList());
            autoRechargeMapper.updateAutoRecurringEvents(updateEvents);
        }

        if (!createQueue.isEmpty()) {
            List<AutoRecurringEvent> createEvents = createQueue.stream()
                    .distinct()
                    .map(d -> AutoRecurringEvent.builder().autoRequestId(id).dayOfPeriod(d).build())
                    .collect(Collectors.toList());
            autoRechargeMapper.saveAutoRecurringEvents(createEvents);
        }
    }

    public AutoRechargeRequestDto getSingleService(String id) {
        AutoRechargeRequest request = autoRechargeMapper.findAutoRechargeById(id);

        if (request == null) {
            throw new ResourceNotFoundException("AutoRechargeService", "Id", id);
        }

        List<AutoRecurringEvent> events = autoRechargeMapper.findEnabledRecurringEventsByAutoId(id);

        if (events == null || events.isEmpty()) {
            throw new ResourceNotFoundException("Event Days/Month", "AutoRecharge", id);
        }

        List<AutoIndividualRequest> individualRequests = autoRechargeMapper.findBulkIndividualRequests(id);

        if (individualRequests == null || individualRequests.isEmpty()) {
            throw new RuntimeException(String.format("No Individual Requests found for AutoRecharge (%s)", id));
        }

        request.setRecipients(individualRequests);

        AutoRechargeRequestDto dto = autoRechargeMapstructMapper.requestToDto(request);

        List<Integer> daysOfPeriod = events.stream()
                .map(AutoRecurringEvent::getDayOfPeriod)
                .collect(Collectors.toList());

        if (request.getRecurringType() == AUTO_RECURRING_WEEKLY_TYPE) {
            dto.setDaysOfWeek(daysOfPeriod);
        } else {
            dto.setDaysOfMonth(daysOfPeriod);
        }

        return dto;
    }

    public PagedDto<ShortAutoRechargeRequestDto> findUserRecharges(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createShortDto(autoRechargeMapper.findAutoRechargeByUserId(ProviderSecurity.getUserId()));
    }

    public PagedDto<NewBulkRechargeRequestDto> getBulkRecharges(String id, Integer pageNumber, Integer pageSize) {
        AutoRechargeRequest request = autoRechargeMapper.findAutoRechargeById(id);

        if (request == null || request.getUserId() == null) {
            throw new ResourceNotFoundException("AutoRechargeRequest", "id or userId", id);
        }

        return newBulkRechargeService.getUserRechargesByAutoRequestId(request.getUserId(), pageNumber, pageSize);
    }

    public void deleteService(String id) {
        autoRechargeMapper.deleteAutoRecharge(id);
    }

    public void runAutoRecharge() {

        Calendar calendar = Calendar.getInstance();

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);

        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        int lastDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        log.info(String.format("Last Day of the Month for %s is %d", months[monthOfYear], lastDayOfMonth));

        Map<String, String> weeklyMap = new HashMap<>();
        weeklyMap.put("dayOfWeek", String.valueOf(dayOfWeek));
        weeklyMap.put("weekId", String.valueOf(weekOfYear));
        weeklyMap.put("userId", ProviderSecurity.getUserId());

        log.info(String.format("Running Weekly Recharge for %s of  Week (%d) of the Year", days[dayOfWeek - 1], weekOfYear));

        runEvents(autoRechargeMapper.todaysWeeklyRuns(weeklyMap), weekOfYear);

        Map<String, String> monthlyMap = new HashMap<>();
        weeklyMap.put("dayOfMonth", String.valueOf(dayOfMonth));
        weeklyMap.put("monthId", String.valueOf(monthOfYear));
        weeklyMap.put("userId", ProviderSecurity.getUserId());

        log.info(String.format("Running Monthly Recharge for Day (%d) of %s of the Year", dayOfMonth, months[monthOfYear]));

        if (lastDayOfMonth == dayOfMonth) {
            runEvents(autoRechargeMapper.lastDayMonthlyRuns(monthlyMap), monthOfYear);
        } else {
            runEvents(autoRechargeMapper.todaysMonthlyRuns(monthlyMap), monthOfYear);
        }
    }

    public PagedDto<ShortAutoRechargeRequestDto> searchByDate(Date date, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<ShortAutoRechargeRequest> requests = autoRechargeMapper.searchByDate(new SearchByDate(date));

        return createShortDto(requests);
    }

    public PagedDto<ShortAutoRechargeRequestDto> searchByName(String name, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<ShortAutoRechargeRequest> requests = autoRechargeMapper.searchByName(new SearchByString(name));

        return createShortDto(requests);
    }

    private PagedDto<ShortAutoRechargeRequestDto> createShortDto(Page<ShortAutoRechargeRequest> requests) {
        PagedDto<ShortAutoRechargeRequestDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) requests.getTotal());
        pagedDto.setPageNumber(requests.getPageNum());
        pagedDto.setPageSize(requests.getPageSize());
        pagedDto.setPages(requests.getPages());
        pagedDto.setList(autoRechargeMapstructMapper.listShortDtoToShort(requests.getResult()));
        return pagedDto;
    }

    private void runEvents(List<AutoRunEvent> events, Integer periodId) {
        if (events != null && !events.isEmpty()) {
           log.info(String.format("Auto Recharge has %d Events", events.size()));
            events.forEach(event -> {

                // Load IndividualRequest and create NewBulkRechargeDto Object
                List<AutoIndividualRequest> individualRequest = autoRechargeMapper.findBulkIndividualRequests(event.getAutoRequestId());

                if (individualRequest == null || individualRequest.isEmpty()) {
                    log.error(String.format("No IndividualRequests in AutoRecharge Request %s", event.getAutoRequestId()));
                } else {
                    log.info(String.format("Running AutoRecharge Request %s Id %s with %d recipients", event.getTitle(), event.getAutoRequestId(), individualRequest.size()));

                    try {
                        NewBulkRechargeRequestDto requestDto = new NewBulkRechargeRequestDto();
                        requestDto.setRecipients(autoRechargeMapstructMapper.listNonAutoToAuto(individualRequest));
                        requestDto.setAutoRequestId(event.getAutoRequestId());
                        requestDto.setPaymentMode("wallet");

                        newBulkRechargeService.saveService(requestDto);
                    } catch (Exception e) {
                        log.error (
                                String.format("Error running Auto Recharge (%s) RequestId (%s) Event (%d) Reason (%s)",
                                event.getTitle(),
                                event.getAutoRequestId(),
                                event.getRecurringEventId(),
                                e.getMessage())
                        );
                    }

                    AutoEventRan autoEventRan = AutoEventRan.builder()
                            .recurringEventId(event.getRecurringEventId())
                            .autoRequestId(event.getAutoRequestId())
                            .periodId(periodId)
                            .build();

                    autoRechargeMapper.saveRanEvent(autoEventRan);
                }
            });
        }
    }
}
