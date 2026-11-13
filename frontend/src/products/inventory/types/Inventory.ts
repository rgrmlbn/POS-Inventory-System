export interface Inventory {
    id: number
    productName: string
    quantity: number
}

export interface InventoryPage {
    content: Inventory[]
    totalPages: number
}