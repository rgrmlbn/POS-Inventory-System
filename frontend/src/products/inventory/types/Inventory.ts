export interface Inventory {
    id: number
    categoryName: string
}

export interface InventoryPage {
    content: Inventory[]
    totalPages: number
}