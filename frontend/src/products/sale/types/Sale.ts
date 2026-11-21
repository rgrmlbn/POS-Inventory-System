export interface SaleItem {
  productId: number;
  productName: string;
  saleQuantity: number;
  unitPrice: number;
  lineTotal: number;
}

export interface Sale {
  id: number;
  receiptNumber: string;
  subtotal: number;
  discount: number;
  totalAmount: number;
  amountPaid: number;
  changeAmount: number;
  paymentMethod:  "CASH" | "CARD" | "EWALLET";
  status: "COMPLETED" | "VOIDED";
  createdAt: string;
  items: SaleItem[];
}

export interface SalePage {
  content: Sale[];
  totalPages: number;
}

export interface SaleItemCreateRequest {
  productId: number;
  quantity: number;
}

export interface SaleCreateRequest {
  items: SaleItemCreateRequest[];
  discount?: number;
  paymentMethod: "CASH" | "CARD" | "EWALLET";
  amountPaid: number;
}
