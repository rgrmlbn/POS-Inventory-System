export interface Category {
  id: number;
  categoryName: string;
}

export interface CategoryPage {
  content: Category[];
  totalPages: number;
}

export interface CategoryCreateRequest {
  categoryName: string;
}

export type CategoryUpdateRequest = Partial<CategoryCreateRequest>;
