export interface Purchase {
  id: number;
  productId: number
  productName: string;
  purchaseQuantity: number;
  unitCost: number;
  totalCost: number;
  supplierName: number;
  note: string;
}

export interface PurchasePage {
  content: Purchase[];
  totalPages: number;
}

export interface PurchaseCreateRequest {
  productId: number;
  purchaseQuantity: number;
  unitCost: number;
  supplierName: string;
  note?: string;
}
