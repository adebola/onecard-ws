package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.ProviderCategoryMapper;
import io.factorialsystems.msscprovider.domain.ProviderCategory;
import io.factorialsystems.msscprovider.mapper.category.ProviderCategoryMapstructMapper;
import io.factorialsystems.msscprovider.dto.provider.ProviderCategoryDto;
import io.factorialsystems.msscprovider.dto.PagedDto;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderCategoryService {
    private final AuditService auditService;
    private final ProviderCategoryMapper categoryMapper;
    private final ProviderCategoryMapstructMapper categoryMapstructMapper;

    private static final String EVENT_CREATE_CATEGORY = "Create Category";
    private static final String EVENT_UPDATE_CATEGORY = "Update Category";

    public PagedDto<ProviderCategoryDto> findProviderCategories(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<ProviderCategory> providerCategories = categoryMapper.findAll();

        return createDto(providerCategories);
    }

    public PagedDto<ProviderCategoryDto> searchProviderCategories(Integer pageNumber, Integer pageSize, String searchString) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<ProviderCategory> providerCategories = categoryMapper.search(searchString);

        return createDto(providerCategories);
    }


    public ProviderCategoryDto findProviderCategoryById(Integer id) {
        return categoryMapstructMapper.toProviderCategoryDto(categoryMapper.findById(id));
    }

    public Integer saveProviderCategory(String userName, ProviderCategoryDto dto) {

        ProviderCategory providerCategory = categoryMapstructMapper.fromProviderCategory(dto);
        providerCategory.setCreatedBy(userName);

        categoryMapper.save(providerCategory);

        String message = String.format("%s Created new Provider Category %s", ProviderSecurity.getUserName(), dto.getCategoryName());
        auditService.auditEvent(message, EVENT_CREATE_CATEGORY);

        return providerCategory.getId();
    }

    public void updateProviderCategory(Integer id, ProviderCategoryDto dto) {
        ProviderCategory providerCategory = categoryMapstructMapper.fromProviderCategory(dto);
        providerCategory.setId(id);

        String message = String.format("%s Updated Provider Category %s", ProviderSecurity.getUserName(), dto.getCategoryName());
        auditService.auditEvent(message, EVENT_UPDATE_CATEGORY);

        categoryMapper.update(providerCategory);

    }

    private PagedDto<ProviderCategoryDto> createDto(Page<ProviderCategory> providerCategories) {
        PagedDto<ProviderCategoryDto> pagedDto = new PagedDto<>();
        pagedDto.setPages(providerCategories.getPages());
        pagedDto.setTotalSize((int) providerCategories.getTotal());
        pagedDto.setPageNumber(providerCategories.getPageNum());
        pagedDto.setPageSize(providerCategories.getPageSize());
        pagedDto.setList(categoryMapstructMapper.toListProviderCategoryDto(providerCategories.getResult()));

        return pagedDto;
    }
}
