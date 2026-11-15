export interface Inventory {
  id: number;
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
