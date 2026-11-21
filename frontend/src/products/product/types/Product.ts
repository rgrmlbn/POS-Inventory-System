export interface Product {
    id: number
    productName: string
    sku: string
    barcode: string
    categoryId: number
    categoryName: string
    costPrice: number
    sellingPrice: number
    reorderLevel: number
    availableQuantity: number
}

export interface ProductPage {
    content: Product[]
    totalPages: number
}


export interface ProductCreateRequest {
  productName: string;
  sku: string;
  barcode: string;
  categoryId: number;
  costPrice: number;
  sellingPrice: number;
  reorderLevel: number;
}

export type ProductUpdateRequest = Partial<ProductCreateRequest>;