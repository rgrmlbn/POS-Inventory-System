export interface Purchase {
  id: number;
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
