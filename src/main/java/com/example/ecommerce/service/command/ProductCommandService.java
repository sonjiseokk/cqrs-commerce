package com.example.ecommerce.service.command;

import com.example.ecommerce.common.DuplicateSlugException;
import com.example.ecommerce.common.ResourceNotFoundException;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.dto.ProductDto;
import com.example.ecommerce.service.mapper.ProductMapper;
import com.example.ecommerce.service.query.ProductQuery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCommandService implements ProductCommandHandler {
    @PersistenceContext
    private EntityManager entityManager;

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
    private final ProductQueryRepository productQueryRepository;
    private final ObjectMapper objectMapper;

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
        createEntity(command, product);

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
    public ProductDto.ProductBasic updateProduct(ProductCommand.UpdateProduct command) throws JsonProcessingException {
        // 0. 패치 조인을 위한 Query 객체 생성
        ProductQuery.GetProduct query = ProductQuery.GetProduct.builder()
                .productId(command.getProductId())
                .build();

        // 1. 기존 엔티티 조회
        Product product = productRepository.findById(command.getProductId()).orElseThrow();

        // 2. 연관 엔티티 업데이트
        updateEntity(command, product);

        // 3. 엔티티 강제 업데이트
        productRepository.save(product);

        return productMapper.toProductBasicDto(product);
    }

    /**
     * Product 삭제 처리
     *
     * @param command
     */
    @Override
    @Transactional
    public void deleteProduct(ProductCommand.DeleteProduct command) {
        // 해당 제품이 없는 경우 예외 처리
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("product", command.getProductId()));

        // 삭제 처리 (소프트)
        product.delete();
    }

    /**
     * Option 업데이트 처리
     *
     * @param command
     * @return
     */
    @Override
    @Transactional
    public ProductDto.Option updateOption(ProductCommand.UpdateOption command) {
        // Product 존재 여부 체크
        if (!productRepository.existsById(command.getProductId())) {
            throw new ResourceNotFoundException("product", command.getProductId());
        }

        // Option 존재 여부 체크
        ProductOption option = productOptionRepository.findById(command.getOptionId())
                .orElseThrow(() -> new ResourceNotFoundException("option", command.getOptionId()));

        // 옵션 업데이트
        option.update(
                command.getName(),
                command.getAdditionalPrice(),
                command.getSku(),
                command.getStock(),
                command.getDisplayOrder()
        );

        return productMapper.toProductOptionDto(option);
    }

    /**
     * Option 삭제 처리
     *
     * @param command
     */
    @Override
    @Transactional
    public void deleteOption(ProductCommand.DeleteOption command) {
        // Product 존재 여부 체크
        if (!productRepository.existsById(command.getProductId())) {
            throw new ResourceNotFoundException("product", command.getProductId());
        }

        // Option 존재 여부 체크
        if (!productOptionRepository.existsById(command.getOptionId())) {
            throw new ResourceNotFoundException("option", command.getOptionId());
        }

        // Option 삭제 처리
        productOptionRepository.deleteById(command.getOptionId());
    }

    /**
     * Image 등록 처리
     *
     * @param command
     * @return
     */
    @Override
    public ProductDto.ImageDetail createImage(ProductCommand.CreateImage command) {
        // Product
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("product", command.getProductId()));

        // Option
        // nullable 이므로 유효한 경우에만 체크
        ProductOption option = null;
        if (command.getOptionId() != null) {
            option = productOptionRepository.findById(command.getOptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("option", command.getOptionId()));
        }

        // Mapper 사용 위해 DTO로 변환
        ProductDto.ImageDetail dto = ProductDto.ImageDetail.builder()
                .id(command.getProductId())
                .url(command.getUrl())
                .altText(command.getAltText())
                .isPrimary(command.isPrimary())
                .displayOrder(command.getDisplayOrder())
                .optionId(command.getOptionId())
                .build();

        // 엔티티 변환
        ProductImage imageEntity = productMapper.toProductImageEntity(dto, product, option);

        // 저장 및 초기화
        productImageRepository.save(imageEntity);

        return dto;
    }

    /**
     * Image 삭제 처리
     */
    @Override
    @Transactional
    public void deleteImage(ProductCommand.DeleteImage command) {
        // product 존재 여부 체크
        if (!productRepository.existsById(command.getProductId())) {
            throw new ResourceNotFoundException("product", command.getProductId());
        }

        // Image 존재 여부 체크
        if (!productImageRepository.existsById(command.getImageId())) {
            throw new ResourceNotFoundException("image", command.getImageId());
        }

        // 삭제 수행
        productImageRepository.deleteById(command.getImageId());
    }

    // ----------------------------------Helper Method------------------------------------------

    /**
     * 엔티티 생성
     * - 연관 관계 업데이트
     */
    private void createEntity(ProductCommand.ProductBase command, Product product) {
        // Seller
        if (command.getSellerId() != null) {
            Seller seller = sellerRepository.findById(command.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller", command.getSellerId()));
            product.connectSeller(seller);
        }

        // Brand
        if (command.getBrandId() != null) {
            Brand brand = brandRepository.findById(command.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", command.getSellerId()));
            product.connectBrand(brand);
        }

        // 영속성 업데이트
        product = productRepository.save(product);

        // Product Detail
        if (command.getDetail() != null) {
            ProductDetail detail = productMapper.toProductDetailEntity(command.getDetail(), product);
            product.connectDetail(detail);
            productDetailRepository.save(detail);
        }

        // Product Price
        if (command.getPrice() != null) {
            ProductPrice price = productMapper.toProductPriceEntity(command.getPrice(), product);
            product.connectPrice(price);
            productPriceRepository.save(price);
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
                product.getCategories().add(pc);
                productCategoryRepository.save(pc);
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

    /**
     * 엔티티 업데이트
     */
    private void updateEntity(ProductCommand.UpdateProduct command, Product product) throws JsonProcessingException {
        // Product
        product.update(
                command.getName(),
                command.getSlug(),
                command.getShortDescription(),
                command.getFullDescription(),
                command.getStatus()
        );

        // Seller
        if (command.getSellerId() != null) {
            Seller seller = sellerRepository.findById(command.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller", command.getSellerId()));
            product.connectSeller(seller);
        }

        // Brand
        if (command.getBrandId() != null) {
            Brand brand = brandRepository.findById(command.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", command.getSellerId()));
            product.connectBrand(brand);
        }

        // 영속성 업데이트
        product = productRepository.save(product);

        // Product Detail
        if (command.getDetail() != null) {
            ProductDetail detail = product.getDetail();
            ProductDto.Detail dto = command.getDetail();

            detail.update(
                    dto.getWeight(),
                    objectMapper.writeValueAsString(dto.getDimensions()),
                    dto.getMaterials(),
                    dto.getCountryOfOrigin(),
                    dto.getWarrantyInfo(),
                    dto.getCareInstructions(),
                    objectMapper.writeValueAsString(dto.getAdditionalInfo())
            );
        }

        // Product Price
        if (command.getPrice() != null) {
            ProductPrice price = product.getPrice();
            ProductDto.Price dto = command.getPrice();

            price.update(
                    dto.getBasePrice(),
                    dto.getSalePrice(),
                    dto.getCostPrice(),
                    dto.getCurrency(),
                    dto.getTaxRate()
            );
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

            // DB 반영
            entityManager.flush();

            // 연관 관계 설정
            for (Category category : categories) {
                boolean isPrimary = command.getCategories().stream()
                        .filter(c -> c.getCategoryId().equals(category.getId()))
                        .findFirst()
                        .map(ProductDto.CategoryBasic::isPrimary)
                        .orElse(false);

                ProductCategory pc = ProductCategory.of(product, category, isPrimary);
                product.getCategories().add(pc);
            }
        }

        // Tag
        if (!command.getTags().isEmpty()) {
            // 기존 태그 초기화
            product.getTags().clear();

            // DB 반영
            entityManager.flush();

            List<Tag> tags = tagRepository.findAllById(command.getTags());

            for (Tag tag : tags) {
                ProductTag productTag = ProductTag.create(product, tag);
                product.getTags().add(productTag);
            }
        }

        // 옵션 그룹들
        if (command.getOptionGroups() != null && !command.getOptionGroups().isEmpty()) {
            updateGroups(command.getOptionGroups(), product);
        }

        // Product Image
        if (command.getImages() != null && !command.getImages().isEmpty()) {
            // 현재 연관관계 초기화 (PUT)
            product.getImages().clear();

            // DB 반영
            entityManager.flush();

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

                product.getImages().add(imageEntity);
            }
        }
    }


    private void updateGroups(List<ProductDto.OptionGroup> optionGroups, Product product) {
        // 현재 옵션 그룹 초기화
        product.getOptionGroups().clear();

        // DB 반영
        entityManager.flush();

        // 현재의 옵션 그룹 -> Map<id, Entity>
        for (ProductDto.OptionGroup groupDto : optionGroups) {
            // 엔티티 생성
            ProductOptionGroup optionGroupEntity = productMapper.toProductOptionGroupEntity(groupDto, product);

            // 하위 Option 필드 업데이트
            updateOptions(optionGroupEntity, groupDto.getOptions());

            product.getOptionGroups().add(optionGroupEntity);
        }

    }

    private void updateOptions(ProductOptionGroup group, List<ProductDto.Option> optionDtos) {
        // 현재 가진 옵션 초기화
        group.getOptions().clear();

        // DB 반영
        entityManager.flush();

        for (ProductDto.Option dto : optionDtos) {
            ProductOption optionEntity = productMapper.toProductOptionEntity(dto, group);

            group.getOptions().add(optionEntity);
        }
    }

}
