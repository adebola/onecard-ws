package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.ProviderCategoryMapper;
import io.factorialsystems.msscprovider.domain.ProviderCategory;
import io.factorialsystems.msscprovider.web.mapper.category.ProviderCategoryMapstructMapper;
import io.factorialsystems.msscprovider.web.model.ProviderCategoryDto;
import io.factorialsystems.msscprovider.web.model.PagedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderCategoryService {
    private final ProviderCategoryMapper categoryMapper;
    private final ProviderCategoryMapstructMapper categoryMapstructMapper;

    public PagedDto<ProviderCategoryDto> findProviderCategories(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<ProviderCategory> providerCategories = categoryMapper.findAll();

        return createDto(providerCategories);
    }

    public PagedDto<ProviderCategoryDto> searchProviderCategories(Integer pageNumber, Integer pageSize, String searchString) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<ProviderCategory> providerCategories = categoryMapper.Search(searchString);

        return createDto(providerCategories);
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


    public ProviderCategoryDto findProviderCategoryById(Integer id) {
        return categoryMapstructMapper.toProviderCategoryDto(categoryMapper.findById(id));
    }

    public Integer saveProviderCategory(String userName, ProviderCategoryDto dto) {

        ProviderCategory providerCategory = categoryMapstructMapper.fromProviderCategory(dto);

        categoryMapper.save(providerCategory);

        return providerCategory.getId();
    }

    public void updateProviderCategory(Integer id, ProviderCategoryDto dto) {
        ProviderCategory providerCategory = categoryMapstructMapper.fromProviderCategory(dto);
        providerCategory.setId(id);

        categoryMapper.update(providerCategory);

    }
}
