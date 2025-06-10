package com.example.ecommerce.service.command;

import com.example.ecommerce.common.DuplicateSlugException;
import com.example.ecommerce.common.ResourceNotFoundException;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.dto.ProductDto;
import com.example.ecommerce.service.mapper.ProductCommandMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCommandService implements ProductCommandHandler {
    private final ProductCommandMapper mapper;
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final SellerRepository sellerRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductCategoryRepository productCategoryRepository;

    /**
     * Product 등록 처리
     * @param command
     * @return
     */
    @Override
    @Transactional
    public ProductDto.ProductBasic createProduct(ProductCommand.CreateProduct command) {
        // 1. 기본 엔티티 생성
        Product product = mapper.toProductEntity(command);

        // 1-1. 중복 슬러그 예외 처리
        if (productRepository.existsBySlug(product.getSlug())) {
            throw new DuplicateSlugException(product.getSlug());
        }

        // 2. 연관관계 연결
        // Seller
        if (command.getSellerId() != null) {
            Seller seller = sellerRepository.findById(command.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller", command.getSellerId()));
            product.addSeller(seller);
        }

        // Brand
        if (command.getBrandId() != null) {
            Brand brand = brandRepository.findById(command.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", command.getSellerId()));
            product.addBrand(brand);
        }

        // 3. 저장 및 id 획득
        product = productRepository.save(product);

        // 상세 정보
        if (command.getDetail() != null) {
            ProductDetail detail = mapper.toProductDetailEntity(command.getDetail(), product);
            productDetailRepository.save(detail);
            product.addDetail(detail);
        }

        // 가격 정보
        if (command.getPrice() != null) {
            ProductPrice price = mapper.toProductPriceEntity(command.getPrice(), product);
            productPriceRepository.save(price);
            product.addProductPrice(price);
        }

        // Category
        if (!command.getCategories().isEmpty()) {
            // ID 리스트 추출
            List<Long> categoryIds = command.getCategories()
                    .stream()
                    .map(ProductDto.CategoryBasic::getCategoryId)
                    .toList();

            List<Category> categories = categoryRepository.findAllById(categoryIds);

            // 연관 관계 설정
            for (Category category : categories) {
                boolean isPrimary = command.getCategories().stream()
                        .filter(c -> c.getCategoryId().equals(category.getId()))
                        .findFirst()
                        .map(ProductDto.CategoryBasic::isPrimary)
                        .orElse(false);

                ProductCategory pc = ProductCategory.of(product, category, isPrimary);
                productCategoryRepository.save(pc);
                product.getCategories().add(pc);
            }
        }

        // Tag
        if (!command.getTags().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(command.getTags());
            product.getTags().addAll(tags);
        }

        // 옵션 그룹들
        if (command.getOptionGroups() != null && !command.getOptionGroups().isEmpty()) {
            for (ProductDto.OptionGroup groupDto : command.getOptionGroups()) {
                ProductOptionGroup group = mapper.toProductOptionGroupEntity(groupDto, product);
                product.getOptionGroups().add(group);

                // 옵션들도 함께 연결
                if (groupDto.getOptions() != null) {
                    for (ProductDto.Option optionDto : groupDto.getOptions()) {
                        ProductOption option = mapper.toProductOptionEntity(optionDto, group);
                        group.getOptions().add(option);
                    }
                }
            }
        }

        // 실제 저장
        productRepository.save(product);

        // 반환 DTO (기본 정보용)
        return mapper.toProductBasicDto(product);
    }

}
