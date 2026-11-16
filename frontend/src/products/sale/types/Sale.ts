
export interface SaleItem {
  productId: number
  productName: string
  saleQuantity: number
  unitPrice: number
  lineTotal: number
} 

export interface Sale {
  id: number
  receiptNumber: string
  subtotal: number
  discount: number
  totalAmount: number
  amountPaid: number
  changeAmount: number
  paymentMethod: string
  status: string
  createdAt: string
  items: SaleItem[]
}

export interface SalePage {
  content: Sale[]
  totalPages: number
}