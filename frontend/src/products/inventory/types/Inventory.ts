export interface Inventory {
  id: number;
  productId: string
  productName: string;
  categoryName: string;
  sku: string;
  quantity: number;
  reorderLevel: number;
}

export interface InventoryPage {
  content: Inventory[];
  totalPages: number;
}

export interface InventoryUpdateRequest {
  quantity: number;
}
