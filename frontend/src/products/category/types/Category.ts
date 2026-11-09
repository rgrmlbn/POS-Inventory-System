export interface Category {
    id: number
    categoryName: string
}

export interface CategoryPage {
    content: Category[]
    totalPages: number
}