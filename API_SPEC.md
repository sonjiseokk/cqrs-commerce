# CQRS 이커머스 플랫폼 - API 명세서

이 문서는 복잡한 연관 관계를 가진 이커머스 시스템의 API 명세를 제공합니다. 각 API에 대한 요청/응답 모델과 에러 응답 모델을 포함하고 있습니다.


## 목차
1. [공통 응답 포맷](#공통-응답-포맷)
2. [에러 응답 모델](#에러-응답-모델)
3. [상품 관리 API](#상품-관리-api)
4. [카테고리 API](#카테고리-api)
5. [메인 페이지 API](#메인-페이지-api)
6. [리뷰 API](#리뷰-api)

## 공통 응답 포맷

성공적인 API 응답은 다음 형식을 따릅니다:

```json
{
 "success": true,
 "data": { /* 응답 데이터 */ },
 "message": "요청이 성공적으로 처리되었습니다."
}
```

페이지네이션이 포함된 응답은 다음 형식을 따릅니다:

```json
{
 "success": true,
 "data": {
   "items": [ /* 응답 데이터 배열 */ ],
   "pagination": {
     "total_items": 100,
     "total_pages": 10,
     "current_page": 1,
     "per_page": 10
   }
 },
 "message": "요청이 성공적으로 처리되었습니다."
}
```

## 에러 응답 모델

에러 발생 시 응답은 다음 형식을 따릅니다:

```json
{
 "success": false,
 "error": {
   "code": "ERROR_CODE",
   "message": "에러 메시지",
   "details": { /* 추가적인 에러 정보 (선택적) */ }
 }
}
```

### 공통 에러 코드

| 코드 | 설명 | HTTP 상태 코드 |
|------|------|--------------|
| INVALID_INPUT | 잘못된 입력 데이터 | 400 |
| RESOURCE_NOT_FOUND | 요청한 리소스를 찾을 수 없음 | 404 |
| UNAUTHORIZED | 인증되지 않은 요청 | 401 |
| FORBIDDEN | 권한이 없는 요청 | 403 |
| CONFLICT | 리소스 충돌 발생 | 409 |
| INTERNAL_ERROR | 서버 내부 오류 | 500 |

## 상품 관리 API

### 상품 등록

**POST /api/products**

새로운 상품을 등록합니다.

**요청 헤더**
```
Content-Type: application/json
Authorization: Bearer {token}
```

**요청 모델**
```json
{
 "name": "슈퍼 편안한 소파",
 "slug": "super-comfortable-sofa",
 "short_description": "최고급 소재로 만든 편안한 소파",
 "full_description": "<p>이 소파는 최고급 소재로 제작되었으며...</p>",
 "seller_id": 1,
 "brand_id": 2,
 "status": "ACTIVE",
 "detail": {
   "weight": 25.5,
   "dimensions": {
     "width": 200,
     "height": 85,
     "depth": 90
   },
   "materials": "가죽, 목재, 폼",
   "country_of_origin": "대한민국",
   "warranty_info": "2년 품질 보증",
   "care_instructions": "마른 천으로 표면을 닦아주세요",
   "additional_info": {
     "assembly_required": true,
     "assembly_time": "30분"
   }
 },
 "price": {
   "base_price": 599000,
   "sale_price": 499000,
   "cost_price": 350000,
   "currency": "KRW",
   "tax_rate": 10
 },
 "categories": [
   {
     "category_id": 5,
     "is_primary": true
   },
   {
     "category_id": 8,
     "is_primary": false
   }
 ],
 "option_groups": [
   {
     "name": "색상",
     "display_order": 1,
     "options": [
       {
         "name": "브라운",
         "additional_price": 0,
         "sku": "SOFA-BRN",
         "stock": 10,
         "display_order": 1
       },
       {
         "name": "블랙",
         "additional_price": 0,
         "sku": "SOFA-BLK",
         "stock": 15,
         "display_order": 2
       }
     ]
   },
   {
     "name": "소재",
     "display_order": 2,
     "options": [
       {
         "name": "천연 가죽",
         "additional_price": 100000,
         "sku": "SOFA-LTHR",
         "stock": 5,
         "display_order": 1
       },
       {
         "name": "인조 가죽",
         "additional_price": 0,
         "sku": "SOFA-FAKE",
         "stock": 20,
         "display_order": 2
       }
     ]
   }
 ],
 "images": [
   {
     "url": "https://example.com/images/sofa1.jpg",
     "alt_text": "브라운 소파 정면",
     "is_primary": true,
     "display_order": 1,
     "option_id": null
   },
   {
     "url": "https://example.com/images/sofa2.jpg",
     "alt_text": "브라운 소파 측면",
     "is_primary": false,
     "display_order": 2,
     "option_id": null
   }
 ],
 "tags": [1, 4, 7]
}
```

**응답 모델 (성공 - 201 Created)**
```json
{
 "success": true,
 "data": {
   "id": 123,
   "name": "슈퍼 편안한 소파",
   "slug": "super-comfortable-sofa",
   "created_at": "2025-04-14T09:30:00Z",
   "updated_at": "2025-04-14T09:30:00Z"
 },
 "message": "상품이 성공적으로 등록되었습니다."
}
```

**에러 응답 예시 (400 Bad Request)**
```json
{
 "success": false,
 "error": {
   "code": "INVALID_INPUT",
   "message": "상품 등록에 실패했습니다.",
   "details": {
     "name": "상품명은 필수 항목입니다.",
     "base_price": "기본 가격은 0보다 커야 합니다."
   }
 }
}
```

### 상품 목록 조회

**GET /api/products**

상품 목록을 조회합니다.

**요청 파라미터**
```
?page=1&perPage=10&sort=created_at:desc&status=ACTIVE&minPrice=10000&maxPrice=100000&category=5&seller=1&brand=2&inStock=true&search=소파
```

| 파라미터 | 타입 | 필수 여부 | 설명 |
|---------|------|----------|------|
| page | int | 아니오 (기본값: 1) | 페이지 번호 |
| perPage | int | 아니오 (기본값: 10) | 페이지당 아이템 수 |
| sort | string | 아니오 (기본값: created_at:desc) | 정렬 기준. 형식: {필드}:{asc\|desc}. 여러 개인 경우 콤마로 구분 |
| status | string | 아니오 | 상품 상태 필터 (ACTIVE, OUT_OF_STOCK, DELETED) |
| minPrice | int | 아니오 | 최소 가격 필터 |
| maxPrice | int | 아니오 | 최대 가격 필터 |
| category | int[] | 아니오 | 카테고리 ID 필터 (여러 개인 경우 콤마로 구분) |
| seller | int | 아니오 | 판매자 ID 필터 |
| brand | int | 아니오 | 브랜드 ID 필터 |
| inStock | boolean | 아니오 | 재고 유무 필터 |
| search | string | 아니오 | 검색어 |

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": {
   "items": [
     {
       "id": 123,
       "name": "슈퍼 편안한 소파",
       "slug": "super-comfortable-sofa",
       "short_description": "최고급 소재로 만든 편안한 소파",
       "base_price": 599000,
       "sale_price": 499000,
       "currency": "KRW",
       "primary_image": {
         "url": "https://example.com/images/sofa1.jpg",
         "alt_text": "브라운 소파 정면"
       },
       "brand": {
         "id": 2,
         "name": "편안가구"
       },
       "seller": {
         "id": 1,
         "name": "홈퍼니처"
       },
       "rating": 4.7,
       "review_count": 128,
       "in_stock": true,
       "status": "ACTIVE",
       "created_at": "2025-04-10T09:30:00Z"
     },
     // ... 추가 상품 항목
   ],
   "pagination": {
     "total_items": 100,
     "total_pages": 10,
     "current_page": 1,
     "per_page": 10
   }
 },
 "message": "상품 목록을 성공적으로 조회했습니다."
}
```

### 상품 상세 조회

**GET /api/products/{id}**

특정 상품의 상세 정보를 조회합니다.

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": {
   "id": 123,
   "name": "슈퍼 편안한 소파",
   "slug": "super-comfortable-sofa",
   "short_description": "최고급 소재로 만든 편안한 소파",
   "full_description": "<p>이 소파는 최고급 소재로 제작되었으며...</p>",
   "seller": {
     "id": 1,
     "name": "홈퍼니처",
     "description": "최고의 가구 전문 판매점",
     "logo_url": "https://example.com/sellers/homefurniture.png",
     "rating": 4.8,
     "contact_email": "contact@homefurniture.com",
     "contact_phone": "02-1234-5678"
   },
   "brand": {
     "id": 2,
     "name": "편안가구",
     "description": "편안함에 집중한 프리미엄 가구 브랜드",
     "logo_url": "https://example.com/brands/comfortfurniture.png",
     "website": "https://comfortfurniture.com"
   },
   "status": "ACTIVE",
   "created_at": "2025-04-10T09:30:00Z",
   "updated_at": "2025-04-14T10:15:00Z",
   "detail": {
     "weight": 25.5,
     "dimensions": {
       "width": 200,
       "height": 85,
       "depth": 90
     },
     "materials": "가죽, 목재, 폼",
     "country_of_origin": "대한민국",
     "warranty_info": "2년 품질 보증",
     "care_instructions": "마른 천으로 표면을 닦아주세요",
     "additional_info": {
       "assembly_required": true,
       "assembly_time": "30분"
     }
   },
   "price": {
     "base_price": 599000,
     "sale_price": 499000,
     "currency": "KRW",
     "tax_rate": 10,
     "discount_percentage": 17
   },
   "categories": [
     {
       "id": 5,
       "name": "소파",
       "slug": "sofa",
       "is_primary": true,
       "parent": {
         "id": 2,
         "name": "거실 가구",
         "slug": "living-room"
       }
     },
     {
       "id": 8,
       "name": "3인용 소파",
       "slug": "3-seater-sofa",
       "is_primary": false,
       "parent": {
         "id": 5,
         "name": "소파",
         "slug": "sofa"
       }
     }
   ],
   "option_groups": [
     {
       "id": 15,
       "name": "색상",
       "display_order": 1,
       "options": [
         {
           "id": 31,
           "name": "브라운",
           "additional_price": 0,
           "sku": "SOFA-BRN",
           "stock": 10,
           "display_order": 1
         },
         {
           "id": 32,
           "name": "블랙",
           "additional_price": 0,
           "sku": "SOFA-BLK",
           "stock": 15,
           "display_order": 2
         }
       ]
     },
     {
       "id": 16,
       "name": "소재",
       "display_order": 2,
       "options": [
         {
           "id": 33,
           "name": "천연 가죽",
           "additional_price": 100000,
           "sku": "SOFA-LTHR",
           "stock": 5,
           "display_order": 1
         },
         {
           "id": 34,
           "name": "인조 가죽",
           "additional_price": 0,
           "sku": "SOFA-FAKE",
           "stock": 20,
           "display_order": 2
         }
       ]
     }
   ],
   "images": [
     {
       "id": 150,
       "url": "https://example.com/images/sofa1.jpg",
       "alt_text": "브라운 소파 정면",
       "is_primary": true,
       "display_order": 1,
       "option_id": null
     },
     {
       "id": 151,
       "url": "https://example.com/images/sofa2.jpg",
       "alt_text": "브라운 소파 측면",
       "is_primary": false,
       "display_order": 2,
       "option_id": null
     }
   ],
   "tags": [
     {
       "id": 1,
       "name": "편안함",
       "slug": "comfort"
     },
     {
       "id": 4,
       "name": "프리미엄",
       "slug": "premium"
     },
     {
       "id": 7,
       "name": "거실 가구",
       "slug": "living-room-furniture"
     }
   ],
   "rating": {
     "average": 4.7,
     "count": 128,
     "distribution": {
       "5": 95,
       "4": 20,
       "3": 10,
       "2": 2,
       "1": 1
     }
   },
   "related_products": [
     {
       "id": 124,
       "name": "패브릭 1인 소파",
       "slug": "fabric-single-sofa",
       "short_description": "작은 공간에 어울리는 패브릭 1인 소파",
       "primary_image": {
         "url": "https://example.com/images/single-sofa.jpg",
         "alt_text": "패브릭 1인 소파"
       },
       "base_price": 299000,
       "sale_price": 259000,
       "currency": "KRW"
     },
     // ... 추가 관련 상품
   ]
 },
 "message": "상품 상세 정보를 성공적으로 조회했습니다."
}
```

**에러 응답 예시 (404 Not Found)**
```json
{
 "success": false,
 "error": {
   "code": "RESOURCE_NOT_FOUND",
   "message": "요청한 상품을 찾을 수 없습니다."
 }
}
```

### 상품 수정

**PUT /api/products/{id}**

특정 상품 정보를 수정합니다.

**요청 헤더**
```
Content-Type: application/json
Authorization: Bearer {token}
```

**요청 모델**
```json
{
 "name": "업데이트된 슈퍼 편안한 소파",
 "slug": "updated-super-comfortable-sofa",
 "short_description": "업데이트된 최고급 소재로 만든 편안한 소파",
 "full_description": "<p>이 소파는 최고급 소재로 제작되었으며...</p>",
 "seller_id": 1,
 "brand_id": 2,
 "status": "ACTIVE",
 "detail": {
   "weight": 25.5,
   "dimensions": {
     "width": 200,
     "height": 85,
     "depth": 90
   },
   "materials": "고급 가죽, 단단한 목재, 고밀도 폼",
   "country_of_origin": "대한민국",
   "warranty_info": "3년 품질 보증",
   "care_instructions": "마른 천으로 표면을 닦아주세요",
   "additional_info": {
     "assembly_required": true,
     "assembly_time": "30분"
   }
 },
 "price": {
   "base_price": 699000,
   "sale_price": 599000,
   "cost_price": 450000,
   "currency": "KRW",
   "tax_rate": 10
 },
 "categories": [
   {
     "category_id": 5,
     "is_primary": true
   },
   {
     "category_id": 8,
     "is_primary": false
   }
 ],
 // 나머지 필드는 상품 등록 요청과 동일
}
```

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": {
   "id": 123,
   "name": "업데이트된 슈퍼 편안한 소파",
   "slug": "updated-super-comfortable-sofa",
   "updated_at": "2025-04-14T11:45:00Z"
 },
 "message": "상품이 성공적으로 수정되었습니다."
}
```

### 상품 삭제

**DELETE /api/products/{id}**

특정 상품을 삭제합니다 (소프트 삭제).

**요청 헤더**
```
Authorization: Bearer {token}
```

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": null,
 "message": "상품이 성공적으로 삭제되었습니다."
}
```

### 상품 옵션 추가

**POST /api/products/{id}/options**

특정 상품에 옵션을 추가합니다.

**요청 헤더**
```
Content-Type: application/json
Authorization: Bearer {token}
```

**요청 모델**
```json
{
 "option_group_id": 15,
 "name": "네이비",
 "additional_price": 20000,
 "sku": "SOFA-NVY",
 "stock": 8,
 "display_order": 3
}
```

**응답 모델 (성공 - 201 Created)**
```json
{
 "success": true,
 "data": {
   "id": 35,
   "option_group_id": 15,
   "name": "네이비",
   "additional_price": 20000,
   "sku": "SOFA-NVY",
   "stock": 8,
   "display_order": 3
 },
 "message": "상품 옵션이 성공적으로 추가되었습니다."
}
```

### [Optional] 상품 옵션 수정

**PUT /api/products/{id}/options/{optionId}**

특정 상품의 옵션을 수정합니다.

**요청 헤더**
```
Content-Type: application/json
Authorization: Bearer {token}
```

**요청 모델**
```json
{
 "name": "네이비 블루",
 "additional_price": 25000,
 "sku": "SOFA-NVBL",
 "stock": 10,
 "display_order": 3
}
```

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": {
   "id": 35,
   "option_group_id": 15,
   "name": "네이비 블루",
   "additional_price": 25000,
   "sku": "SOFA-NVBL",
   "stock": 10,
   "display_order": 3
 },
 "message": "상품 옵션이 성공적으로 수정되었습니다."
}
```

### 상품 옵션 삭제

**DELETE /api/products/{id}/options/{optionId}**

특정 상품의 옵션을 삭제합니다.

**요청 헤더**
```
Authorization: Bearer {token}
```

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": null,
 "message": "상품 옵션이 성공적으로 삭제되었습니다."
}
```

### 상품 이미지 추가

**POST /api/products/{id}/images**

특정 상품에 이미지를 추가합니다.

**요청 헤더**
```
Content-Type: application/json
Authorization: Bearer {token}
```

**요청 모델**
```json
{
 "url": "https://example.com/images/sofa3.jpg",
 "alt_text": "네이비 소파 측면",
 "is_primary": false,
 "display_order": 3,
 "option_id": 35
}
```

**응답 모델 (성공 - 201 Created)**
```json
{
 "success": true,
 "data": {
   "id": 152,
   "url": "https://example.com/images/sofa3.jpg",
   "alt_text": "네이비 소파 측면",
   "is_primary": false,
   "display_order": 3,
   "option_id": 35
 },
 "message": "상품 이미지가 성공적으로 추가되었습니다."
}
```

## 카테고리 API

### 카테고리 목록 조회

**GET /api/categories**

전체 카테고리 목록을 계층 구조로 조회합니다.

**요청 파라미터**
```
?level=1
```

| 파라미터 | 타입 | 필수 여부 | 설명 |
|---------|------|----------|------|
| level | int | 아니오 | 카테고리 레벨 필터 (1: 대분류, 2: 중분류, 3: 소분류) |

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": [
   {
     "id": 1,
     "name": "가구",
     "slug": "furniture",
     "description": "다양한 가구 제품",
     "level": 1,
     "image_url": "https://example.com/categories/furniture.jpg",
     "children": [
       {
         "id": 2,
         "name": "거실 가구",
         "slug": "living-room",
         "description": "거실용 가구",
         "level": 2,
         "image_url": "https://example.com/categories/living-room.jpg",
         "children": [
           {
             "id": 5,
             "name": "소파",
             "slug": "sofa",
             "description": "다양한 스타일의 소파",
             "level": 3,
             "image_url": "https://example.com/categories/sofa.jpg"
           },
           // ... 추가 하위 카테고리
         ]
       },
       // ... 추가 중분류 카테고리
     ]
   },
   // ... 추가 대분류 카테고리
 ],
 "message": "카테고리 목록을 성공적으로 조회했습니다."
}
```

### 특정 카테고리의 상품 목록 조회

**GET /api/categories/{id}/products**

특정 카테고리에 속한 상품 목록을 조회합니다.

**요청 파라미터**
```
?page=1&perPage=10&sort=created_at:desc&includeSubcategories=true
```

| 파라미터 | 타입 | 필수 여부 | 설명 |
|---------|------|----------|------|
| page | int | 아니오 (기본값: 1) | 페이지 번호 |
| perPage | int | 아니오 (기본값: 10) | 페이지당 아이템 수 |
| sort | string | 아니오 (기본값: created_at:desc) | 정렬 기준. 형식: {필드}:{asc\|desc} |
| includeSubcategories | boolean | 아니오 (기본값: true) | 하위 카테고리 포함 여부 |

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": {
   "category": {
     "id": 5,
     "name": "소파",
     "slug": "sofa",
     "description": "다양한 스타일의 소파",
     "level": 3,
     "image_url": "https://example.com/categories/sofa.jpg",
     "parent": {
       "id": 2,
       "name": "거실 가구",
       "slug": "living-room"
     }
   },
   "items": [
     {
       "id": 123,
       "name": "슈퍼 편안한 소파",
       "slug": "super-comfortable-sofa",
       "short_description": "최고급 소재로 만든 편안한 소파",
       "base_price": 599000,
       "sale_price": 499000,
       "currency": "KRW",
       "primary_image": {
         "url": "https://example.com/images/sofa1.jpg",
         "alt_text": "브라운 소파 정면"
       },
       "brand": {
         "id": 2,
         "name": "편안가구"
       },
       "seller": {
         "id": 1,
         "name": "홈퍼니처"
       },
       "rating": 4.7,
       "review_count": 128,
       "in_stock": true,
       "status": "ACTIVE",
       "created_at": "2025-04-10T09:30:00Z"
     },
     // ... 추가 상품 항목
   ],
   "pagination": {
     "total_items": 45,
     "total_pages": 5,
     "current_page": 1,
     "per_page": 10
   }
 },
 "message": "카테고리 상품 목록을 성공적으로 조회했습니다."
}
```

## 메인 페이지 API

### 메인 페이지 상품 및 카테고리 목록 조회

**GET /api/main**

메인 페이지용 상품 및 카테고리 목록을 조회합니다.

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": {
   "new_products": [
     {
       "id": 123,
       "name": "슈퍼 편안한 소파",
       "slug": "super-comfortable-sofa",
       "short_description": "최고급 소재로 만든 편안한 소파",
       "base_price": 599000,
       "sale_price": 499000,
       "currency": "KRW",
       "primary_image": {
         "url": "https://example.com/images/sofa1.jpg",
         "alt_text": "브라운 소파 정면"
       },
       "brand": {
         "id": 2,
         "name": "편안가구"
       },
       "seller": {
         "id": 1,
         "name": "홈퍼니처"
       },
       "rating": 4.7,
       "review_count": 128,
       "in_stock": true,
       "status": "ACTIVE",
       "created_at": "2025-04-10T09:30:00Z"
     },
     // ... 추가 신규 상품
   ],
   "popular_products": [
     {
       "id": 110,
       "name": "클래식 서재 책상",
       "slug": "classic-study-desk",
       "short_description": "견고한 원목으로 만든 클래식 서재 책상",
       "base_price": 450000,
       "sale_price": 399000,
       "currency": "KRW",
       "primary_image": {
         "url": "https://example.com/images/desk1.jpg",
         "alt_text": "클래식 서재 책상"
       },
       "brand": {
         "id": 3,
         "name": "모던홈"
       },
       "seller": {
         "id": 1,
         "name": "홈퍼니처"
       },
       "rating": 4.9,
       "review_count": 50,
       "in_stock": true,
       "status": "ACTIVE",
       "created_at": "2025-02-15T14:30:00Z"
     },
     // ... 추가 인기 상품
   ],
   "featured_categories": [
     {
       "id": 2,
       "name": "거실 가구",
       "slug": "living-room",
       "image_url": "https://example.com/categories/living-room.jpg",
       "product_count": 120
     },
     {
       "id": 3,
       "name": "침실 가구",
       "slug": "bedroom",
       "image_url": "https://example.com/categories/bedroom.jpg",
       "product_count": 85
     },
     // ... 추가 주요 카테고리
   ]
 },
 "message": "메인 페이지 상품 목록을 성공적으로 조회했습니다."
}
```

## 리뷰 API

### 상품 리뷰 조회

**GET /api/products/{id}/reviews**

특정 상품의 리뷰 목록을 조회합니다.

**요청 파라미터**
```
?page=1&perPage=10&sort=created_at:desc&rating=4
```

| 파라미터 | 타입 | 필수 여부 | 설명 |
|---------|------|----------|------|
| page | int | 아니오 (기본값: 1) | 페이지 번호 |
| perPage | int | 아니오 (기본값: 10) | 페이지당 아이템 수 |
| sort | string | 아니오 (기본값: created_at:desc) | 정렬 기준. 형식: {필드}:{asc\|desc} |
| rating | int | 아니오 | 평점 필터 (1-5) |

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": {
   "items": [
     {
       "id": 1500,
       "user": {
         "id": 250,
         "name": "홍길동",
         "avatar_url": "https://example.com/avatars/user250.jpg"
       },
       "rating": 5,
       "title": "완벽한 소파입니다!",
       "content": "배송도 빠르고 품질도 매우 좋습니다. 색상도 사진과 동일하고 조립도 쉬웠어요.",
       "created_at": "2025-04-12T15:30:00Z",
       "updated_at": "2025-04-12T15:30:00Z",
       "verified_purchase": true,
       "helpful_votes": 12
     },
     // ... 추가 리뷰
   ],
   "summary": {
     "average_rating": 4.7,
     "total_count": 128,
     "distribution": {
       "5": 95,
       "4": 20,
       "3": 10,
       "2": 2,
       "1": 1
     }
   },
   "pagination": {
     "total_items": 128,
     "total_pages": 13,
     "current_page": 1,
     "per_page": 10
   }
 },
 "message": "상품 리뷰를 성공적으로 조회했습니다."
}
```

### 리뷰 작성

**POST /api/products/{id}/reviews**

특정 상품에 리뷰를 작성합니다.

**요청 헤더**
```
Content-Type: application/json
Authorization: Bearer {token}
```

**요청 모델**
```json
{
 "rating": 5,
 "title": "완벽한 소파입니다!",
 "content": "배송도 빠르고 품질도 매우 좋습니다. 색상도 사진과 동일하고 조립도 쉬웠어요."
}
```

**응답 모델 (성공 - 201 Created)**
```json
{
 "success": true,
 "data": {
   "id": 1500,
   "user": {
     "id": 250,
     "name": "홍길동",
     "avatar_url": "https://example.com/avatars/user250.jpg"
   },
   "rating": 5,
   "title": "완벽한 소파입니다!",
   "content": "배송도 빠르고 품질도 매우 좋습니다. 색상도 사진과 동일하고 조립도 쉬웠어요.",
   "created_at": "2025-04-14T16:45:00Z",
   "updated_at": "2025-04-14T16:45:00Z",
   "verified_purchase": true,
   "helpful_votes": 0
 },
 "message": "리뷰가 성공적으로 등록되었습니다."
}
```

### 리뷰 수정

**PUT /api/reviews/{id}**

특정 리뷰를 수정합니다.

**요청 헤더**
```
Content-Type: application/json
Authorization: Bearer {token}
```

**요청 모델**
```json
{
 "rating": 4,
 "title": "좋은 소파입니다!",
 "content": "배송도 빠르고 품질도 좋습니다. 다만 색상이 사진보다 약간 어둡습니다."
}
```

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": {
   "id": 1500,
   "rating": 4,
   "title": "좋은 소파입니다!",
   "content": "배송도 빠르고 품질도 좋습니다. 다만 색상이 사진보다 약간 어둡습니다.",
   "updated_at": "2025-04-14T17:30:00Z"
 },
 "message": "리뷰가 성공적으로 수정되었습니다."
}
```

**에러 응답 예시 (403 Forbidden)**
```json
{
 "success": false,
 "error": {
   "code": "FORBIDDEN",
   "message": "다른 사용자의 리뷰를 수정할 권한이 없습니다."
 }
}
```

### 리뷰 삭제

**DELETE /api/reviews/{id}**

특정 리뷰를 삭제합니다.

**요청 헤더**
```
Authorization: Bearer {token}
```

**응답 모델 (성공 - 200 OK)**
```json
{
 "success": true,
 "data": null,
 "message": "리뷰가 성공적으로 삭제되었습니다."
}
```

**에러 응답 예시 (403 Forbidden)**
```json
{
 "success": false,
 "error": {
   "code": "FORBIDDEN",
   "message": "다른 사용자의 리뷰를 삭제할 권한이 없습니다."
 }
}
```

## 공통 에러 응답 상세

### 400 Bad Request (INVALID_INPUT)

```json
{
 "success": false,
 "error": {
   "code": "INVALID_INPUT",
   "message": "입력 데이터가 유효하지 않습니다.",
   "details": {
     "name": "상품명은 필수 항목입니다.",
     "base_price": "기본 가격은 0보다 커야 합니다.",
     "categories": "최소 하나의 카테고리를 지정해야 합니다."
   }
 }
}
```


### 403 Forbidden (FORBIDDEN)

```json
{
 "success": false,
 "error": {
   "code": "FORBIDDEN",
   "message": "해당 작업을 수행할 권한이 없습니다."
 }
}
```

### 404 Not Found (RESOURCE_NOT_FOUND)

```json
{
 "success": false,
 "error": {
   "code": "RESOURCE_NOT_FOUND",
   "message": "요청한 리소스를 찾을 수 없습니다.",
   "details": {
     "resourceType": "Product",
     "resourceId": "123"
   }
 }
}
```

### 409 Conflict (CONFLICT)

```json
{
 "success": false,
 "error": {
   "code": "CONFLICT",
   "message": "리소스 충돌이 발생했습니다.",
   "details": {
     "field": "slug",
     "value": "super-comfortable-sofa",
     "message": "해당 슬러그는 이미 사용 중입니다."
   }
 }
}
```
