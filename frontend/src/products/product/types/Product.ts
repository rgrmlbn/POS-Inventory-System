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
    imageUrl: string | null
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
  imageUrl: string;
  imagePublicId: string;
}

export type ProductCreateFormValues = Omit<
  ProductCreateRequest,
  "imageUrl" | "imagePublicId"
>;

export interface ProductCreateSubmission extends ProductCreateFormValues {
  image: File;
}

export interface ProductUpdateSubmission extends Partial<ProductCreateFormValues> {
  imageUrl?: string;
  imagePublicId?: string;
  image?: File;
}

export interface CloudinaryUploadSignature {
  cloudName: string;
  apiKey: string;
  uploadPreset: string;
  publicId: string;
  timestamp: number;
  signature: string;
}