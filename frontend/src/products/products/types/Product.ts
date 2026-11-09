export interface Product {
    id: number
    productName: string
    sku: string
    barcode: string
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
