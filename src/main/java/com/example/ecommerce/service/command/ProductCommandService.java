package com.example.ecommerce.service.command;

import com.example.ecommerce.common.DuplicateSlugException;
import com.example.ecommerce.common.ResourceNotFoundException;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.dto.ProductDto;
import com.example.ecommerce.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCommandService implements ProductCommandHandler {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final SellerRepository sellerRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ProductPriceRepository productPriceRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductImageRepository productImageRepository;

    /**
     * Product 등록 처리
     *
     * @param command
     * @return
     */
    @Override
    @Transactional
    public ProductDto.ProductBasic createProduct(ProductCommand.CreateProduct command) {
        // 1. 기본 엔티티 생성
        Product product = productMapper.toProductEntity(command);

        // 1-1. 중복 슬러그 예외 처리
        if (productRepository.existsBySlug(product.getSlug())) {
            throw new DuplicateSlugException(product.getSlug());
        }

        // 2. 연관관계 연결
        connectEntity(command, product);

        // 3. 저장 및 id 획득
        product = productRepository.save(product);

        // 반환 DTO (기본 정보용)
        return productMapper.toProductBasicDto(product);
    }

    /**
     * Product 업데이트 처리
     *
     * @param command
     * @return
     */
    @Override
    @Transactional
    public ProductDto.ProductBasic updateProduct(Long productId, ProductCommand.UpdateProduct command) {
        // 0. 중복 슬러그 예외 처리
        if (productRepository.existsBySlug(command.getSlug())) {
            throw new DuplicateSlugException(command.getSlug());
        }

        // 1. 기존 엔티티 조회
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        // 2. 연관관계 연결
        connectEntity(command, product);

        // 3. 엔티티 강제 업데이트
        productRepository.save(product);

        return productMapper.toProductBasicDto(product);
    }

    /**
     * 공통 메소드
     * <p>
     * - 연관 관계 업데이트
     *
     * @param command
     * @param product
     */
    private void connectEntity(ProductCommand.ProductBase command, Product product) {
        // Seller
        if (command.getSellerId() != null) {
            Seller seller = sellerRepository.findById(command.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller", command.getSellerId()));
            product.updateSeller(seller);
        }

        // Brand
        if (command.getBrandId() != null) {
            Brand brand = brandRepository.findById(command.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", command.getSellerId()));
            product.updateBrand(brand);
        }

        // 영속성 업데이트
        product = productRepository.save(product);

        // Product Detail
        if (command.getDetail() != null) {
            ProductDetail detail = productMapper.toProductDetailEntity(command.getDetail(), product);
            productDetailRepository.save(detail);
            product.updateDetail(detail);
        }

        // Product Price
        if (command.getPrice() != null) {
            ProductPrice price = productMapper.toProductPriceEntity(command.getPrice(), product);
            productPriceRepository.save(price);
            product.updateProductPrice(price);
        }

        // Category
        if (!command.getCategories().isEmpty()) {
            // ID 리스트 추출
            List<Long> categoryIds = command.getCategories()
                    .stream()
                    .map(ProductDto.CategoryBasic::getCategoryId)
                    .toList();

            List<Category> categories = categoryRepository.findAllById(categoryIds);

            product.getCategories().clear(); // 연관관계 초기화

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

            for (Tag tag : tags) {
                ProductTag productTag = ProductTag.create(product, tag);
                product.getTags().add(productTag);
                productTagRepository.save(productTag);
            }
        }

        // 옵션 그룹들
        if (command.getOptionGroups() != null && !command.getOptionGroups().isEmpty()) {
            for (ProductDto.OptionGroup groupDto : command.getOptionGroups()) {
                ProductOptionGroup group = productMapper.toProductOptionGroupEntity(groupDto, product);
                product.getOptionGroups().add(group);

                // 옵션들도 함께 연결
                if (groupDto.getOptions() != null) {
                    for (ProductDto.Option optionDto : groupDto.getOptions()) {
                        ProductOption option = productMapper.toProductOptionEntity(optionDto, group);
                        group.getOptions().add(option);
                    }
                }
            }
        }

        // Product Image
        if (command.getImages() != null && !command.getImages().isEmpty()) {
            for (ProductDto.ImageDetail imageDto : command.getImages()) {
                ProductImage imageEntity;
                // optionId 있는 경우
                if (imageDto.getOptionId() != null) {
                    // Option 조회 후, 연관관계 설정
                    ProductOption option = productOptionRepository.findById(imageDto.getOptionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Option", imageDto.getOptionId()));
                    imageEntity = productMapper.toProductImageEntity(imageDto, product, option);

                    option.getImages().add(imageEntity);
                } else {
                    // optionId 없는 경우
                    imageEntity = productMapper.toProductImageEntity(imageDto, product, null);
                }

                // 명시적 save
                productImageRepository.save(imageEntity);
            }
        }
    }

}
