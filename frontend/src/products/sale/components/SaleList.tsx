import { Link } from "react-router-dom";

import type { Sale } from "../types/Sale";

interface SaleListProps {
  sales: Sale[];
  loading: boolean;
  error: unknown;
}

export function SaleList({ sales, loading, error }: SaleListProps) {
  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load sales.</p>;
  if (sales.length === 0) return <p>No sales found.</p>;

  return (
    <>
      {sales.map((sale) => (
        <div key={sale.id}>
          <h2>Receipt: {sale.receiptNumber}</h2>
          <h3>Details:</h3>
          <h4>Sub Total: {sale.subtotal}</h4>
          <h4>
            Discount: {sale.discount === 0 ? "No Discount" : sale.discount}
          </h4>
          <h4>Total Amount: {sale.totalAmount}</h4>
          {/* <h4>Amount Paid By Buyer: {sale.amountPaid}</h4> */}
          {/* <h4>Changed Amount: {sale.changeAmount}</h4> */}
          <h4>Payment Method: {sale.paymentMethod}</h4>
          <h4>Status: {sale.status}</h4>
          {/* <h4>Created Date: {sale.createdAt}</h4> */}
          <Link to={`/sales/${sale.id}`}>View sale items.</Link>
        </div>
      ))}
    </>
  );
}
