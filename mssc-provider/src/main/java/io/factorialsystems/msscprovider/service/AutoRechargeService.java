package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.AutoRechargeMapper;
import io.factorialsystems.msscprovider.domain.query.SearchByDate;
import io.factorialsystems.msscprovider.domain.query.SearchByString;
import io.factorialsystems.msscprovider.domain.rechargerequest.*;
import io.factorialsystems.msscprovider.dto.DateRangeDto;
import io.factorialsystems.msscprovider.dto.PagedDto;
import io.factorialsystems.msscprovider.dto.recharge.*;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import io.factorialsystems.msscprovider.mapper.recharge.AutoRechargeMapstructMapper;
import io.factorialsystems.msscprovider.service.bulkrecharge.NewBulkRechargeService;
import io.factorialsystems.msscprovider.service.file.ExcelReader;
import io.factorialsystems.msscprovider.service.file.ExcelWriter;
import io.factorialsystems.msscprovider.service.file.FileUploader;
import io.factorialsystems.msscprovider.service.file.UploadFile;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoRechargeService {
    private final ExcelWriter excelWriter;
    private final FileUploader fileUploader;
    private final AutoRechargeMapper autoRechargeMapper;
    private final NewBulkRechargeService newBulkRechargeService;
    private final AutoRechargeMapstructMapper autoRechargeMapstructMapper;

    public static final int AUTO_RECURRING_WEEKLY_TYPE = 1;
    public static final int AUTO_RECURRING_MONTHLY_TYPE = 2;

    private static final String[] days = {
            "SUNDAY",
            "MONDAY",
            "TUESDAY",
            "WEDNESDAY",
            "THURSDAY",
            "FRIDAY",
            "SATURDAY"
    };
    private static final String[] months = {
            "JANUARY",
            "FEBRUARY",
            "MARCH",
            "APRIL",
            "MAY",
            "JUNE",
            "JULY",
            "AUGUST",
            "SEPTEMBER",
            "OCTOBER",
            "NOVEMBER",
            "DECEMBER"
    };

    public AutoRechargeResponseDto uploadRecharge(AutoUploadFileRechargeRequestDto dto, MultipartFile file) {
        log.info("Uploading AutoRecharge Bulk Excel File...");

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
            throw new RuntimeException("AutoRecharge saveService Days of Week or Days of Month must be specified");
        }

        if (daysOfPeriod == null || daysOfPeriod.isEmpty()) {
            final String errorMessage = String.format("AutoRecharge saveService No days of the Week or days of the Month specified for AutoRecharge %s, for Type %d", id, request.getRecurringType());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        if (request.getRecipients() == null || request.getRecipients().isEmpty()) {
            final String errorMessage = String.format("AutoRecharge saveService No recipients for AutoRecharge %s, for Type %d", id, request.getRecurringType());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        autoRechargeMapper.saveAutoRecharge(request);

        List<AutoRecurringEvent> events = daysOfPeriod.stream()
                .distinct()
                .map(d -> AutoRecurringEvent.builder().autoRequestId(id).dayOfPeriod(d).build())
                .collect(Collectors.toList());
        autoRechargeMapper.saveAutoRecurringEvents(events);

        request.getRecipients().forEach(recipient -> recipient.setAutoRequestId(id));

        autoRechargeMapper.saveRecipients(request.getRecipients());
        log.info("AutoRecharge {}, saved successfully by {}", id, ProviderSecurity.getUserName());

        return AutoRechargeResponseDto.builder()
                .id(id)
                .message(String.format("Auto Recharge Submitted reference is %s", request.getId()))
                .paymentMode("wallet")
                .build();
    }

    @Transactional
    public void updateService(String id, AutoRechargeRequestDto dto) {

        // Convert AutoRechargeRequest to dto and set id
        AutoRechargeRequest request = autoRechargeMapstructMapper.dtoToRequest(dto);
        request.setId(id);


        // Extract the List of Recurring Weekly or Monthly days
        List<Integer> daysOfPeriod = null;

        if (dto.getDaysOfWeek() != null && !dto.getDaysOfWeek().isEmpty()) {
            request.setRecurringType(AUTO_RECURRING_WEEKLY_TYPE);
            daysOfPeriod = dto.getDaysOfWeek();
        } else if (dto.getDaysOfMonth() != null && !dto.getDaysOfMonth().isEmpty()) {
            request.setRecurringType(AUTO_RECURRING_MONTHLY_TYPE);
            daysOfPeriod = dto.getDaysOfMonth();
        } else {
            throw new RuntimeException("AutoRecharge updateService Nothing to AutoSchedule Days of Week or Days of Month must be specified");
        }

        if (daysOfPeriod == null || daysOfPeriod.isEmpty()) {
            final String errorMessage = String.format("AutoRecharge updateService No days of the Week or days of the Month specified for AutoRecharge %s, for Type %d", id, request.getRecurringType());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        if (request.getRecipients() == null || request.getRecipients().isEmpty()) {
            final String errorMessage = String.format("AutoRecharge updateService No recipients for AutoRecharge %s, for Type %d", id, request.getRecurringType());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        // Save the AutoRechargeRequest
        autoRechargeMapper.updateAutoRecharge(request);

        // Disable and Load all Recurring Days (Weekly or Monthly) for the AutoRechargeRequest
        List<AutoRecurringEvent> currentEvents = autoRechargeMapper.disableAndLoadRecurringEventsByAutoId(id);

        List<Integer> enableQueue = new ArrayList<>();
        List<Integer> createQueue = new ArrayList<>();

        // For the new days submitted for the respective period (Weekly or Monthly)
        // if the days submitted in the request are in the disabled events above re-enable it, i.e. push into enableQueue
        // else if the days submitted are not in the disabled events, hence it is a new request,
        // create new day for the request i.e. push in the CreateQueue
        daysOfPeriod.forEach(day -> {
            currentEvents.stream()
                    .filter(e -> Objects.equals(e.getDayOfPeriod(), day))
                    .findFirst()
                    .ifPresentOrElse(
                            (value) -> {
                                enableQueue.add(value.getId());
                            },
                            () -> {
                                createQueue.add(day);
                            }
                    );
        });

        // Re-Enable days in Period in the database
        if (!enableQueue.isEmpty()) {
            List<AutoRecurringEvent> updateEvents = enableQueue.stream()
                    .distinct()
                    .map(d -> AutoRecurringEvent.builder().id(d).disabled(false).build())
                    .collect(Collectors.toList());
            autoRechargeMapper.updateAutoRecurringEvents(updateEvents);
        }

        // Create new days in period in the database
        if (!createQueue.isEmpty()) {
            List<AutoRecurringEvent> createEvents = createQueue.stream()
                    .distinct()
                    .map(d -> AutoRecurringEvent.builder().autoRequestId(id).dayOfPeriod(d).build())
                    .collect(Collectors.toList());
            autoRechargeMapper.saveAutoRecurringEvents(createEvents);
        }

        // Update Recipients, by deleting existing recipients and re-populating with submission
        autoRechargeMapper.deleteRecipientsByAutoRechargeId(id);
        request.getRecipients().forEach(recipient -> recipient.setAutoRequestId(id));
        autoRechargeMapper.saveRecipients(request.getRecipients());
    }

    public AutoRechargeRequestDto getSingleService(String id) {
        AutoRechargeRequest request = autoRechargeMapper.findAutoRechargeById(id);

        log.info("Retrieving AutoRecharge {}", id);

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

    @Transactional
    public void deleteService(String id) {
        log.info("Deleting AutoRecharge {}", id);
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

        Map<String, String> weeklyMap = new HashMap<>();
        weeklyMap.put("dayOfWeek", String.valueOf(dayOfWeek));
        weeklyMap.put("weekId", String.valueOf(weekOfYear));

        List<AutoRunEvent> autoWeeklyRunEvents = autoRechargeMapper.todaysWeeklyRuns(weeklyMap);
        log.info("Running Weekly Recharge for {} of Week {} of the Year, Found {} to run",
                days[dayOfWeek - 1], weekOfYear, autoWeeklyRunEvents.size());
        if (!autoWeeklyRunEvents.isEmpty()) {
            runEvents(autoWeeklyRunEvents, weekOfYear);
        }

        Map<String, String> monthlyMap = new HashMap<>();
        monthlyMap.put("dayOfMonth", String.valueOf(dayOfMonth));
        monthlyMap.put("monthId", String.valueOf(monthOfYear));

        List<AutoRunEvent> autoMonthlyRunEvents;

        if (lastDayOfMonth == dayOfMonth) {
            autoMonthlyRunEvents = autoRechargeMapper.lastDayMonthlyRuns(monthlyMap);
        } else {
            autoMonthlyRunEvents = autoRechargeMapper.todaysMonthlyRuns(monthlyMap);
        }

        log.info("Running Monthly Recharge for Day {} of {}  for the Year, Found {} to run",
                dayOfMonth, months[monthOfYear], autoMonthlyRunEvents.size());
        if (!autoMonthlyRunEvents.isEmpty()) {
            runEvents(autoMonthlyRunEvents, monthOfYear);
        }
    }

    public PagedDto<ShortAutoRechargeRequestDto> searchByDate(Date date, Integer pageNumber, Integer pageSize) {
        log.info("AutoRecharge search by Date {}", date);
        PageHelper.startPage(pageNumber, pageSize);
        Page<ShortAutoRechargeRequest> requests = autoRechargeMapper.searchByDate(new SearchByDate(date));

        return createShortDto(requests);
    }

    public PagedDto<ShortAutoRechargeRequestDto> searchByName(String name, Integer pageNumber, Integer pageSize) {
        log.info("AutoRecharge search by Name {}", name);
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
            log.info("Auto Recharge has {} Events", events.size());
            events.forEach(event -> {

                // Load IndividualRequest and create NewBulkRechargeDto Object
                List<AutoIndividualRequest> individualRequest =
                        autoRechargeMapper.findBulkIndividualRequests(event.getAutoRequestId());

                if (individualRequest == null || individualRequest.isEmpty()) {
                    log.error("No IndividualRequests in AutoRecharge Request {}", event.getAutoRequestId());
                } else {
                    log.info("Running AutoRecharge Request {} Id {} with {} recipients for User {}",
                            event.getTitle(), event.getAutoRequestId(), individualRequest.size(), event.getUserId());

                    try {
                        NewBulkRechargeRequestDto requestDto = new NewBulkRechargeRequestDto();
                        requestDto.setRecipients(autoRechargeMapstructMapper.listNonAutoToAuto(individualRequest));
                        requestDto.setAutoRequestId(event.getAutoRequestId());
                        requestDto.setPaymentMode("wallet");

                        newBulkRechargeService.saveService(requestDto, Optional.of(event.getUserId()));
                    } catch (Exception e) {
                        log.error(
                                String.format("Error running Auto Recharge (%s) RequestId (%s) Event (%d) Reason (%s)",
                                        event.getTitle(),
                                        event.getAutoRequestId(),
                                        event.getRecurringEventId(),
                                        e.getMessage())
                        );
                    }
                }

                AutoEventRan autoEventRan = AutoEventRan.builder()
                        .recurringEventId(event.getRecurringEventId())
                        .autoRequestId(event.getAutoRequestId())
                        .periodId(periodId)
                        .build();

                log.info("Saving Event Ran {}", autoEventRan);

                autoRechargeMapper.saveRanEvent(autoEventRan);
            });
        }
    }

    public InputStreamResource getRechargeByDateRange(DateRangeDto dto) {
        dto.setId(ProviderSecurity.getUserId());

        String title = null;

        final String pattern = "EEEEE dd MMMMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            title = String.format("Auto Recharge Download for User %s Date Range %s to %s", dto.getId(),
                    simpleDateFormat.format(dto.getStartDate()), simpleDateFormat.format(dto.getEndDate()));
        } else if (dto.getStartDate() != null) {
            title = String.format("Auto Recharge Download for User %s Date %s",
                    dto.getId(), simpleDateFormat.format(dto.getStartDate()));
        } else {
            title = String.format("Auto Recharge Download for User %s", dto.getId());
        }
        List<ShortAutoRechargeRequest> requests = autoRechargeMapper.findByUserIdAndDateRange(dto);

        return new InputStreamResource(excelWriter.autoRequestToExcel(requests, title));
    }
}
