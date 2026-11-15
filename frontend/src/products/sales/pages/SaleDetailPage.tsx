// pages/ProductDetailPage.tsx
import { useParams, Link } from "react-router-dom";
import { useSaleQuery } from "../hooks/useSaleQuery";

export function SaleDetailPage() {
  const { id } = useParams();
  const { sale, loading, error } = useSaleQuery(Number(id));

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load sale.</p>;
  if (!sale) return <p>Sale not found.</p>;

  return (
    <div>
      <Link to="/sales">← Back to sales</Link>

      <h2>Receipt: {sale.receiptNumber}</h2>
      <h3>Details:</h3>
      <h4>Sub Total: {sale.subtotal}</h4>
      <h4>Discount: {sale.discount === 0 ? "No Discount" : sale.discount}</h4>
      <h4>Total Amount: {sale.totalAmount}</h4>
      {<h4>Amount Paid By Buyer: {sale.amountPaid}</h4>}
      {<h4>Changed Amount: {sale.changeAmount}</h4>}
      <h4>Payment Method: {sale.paymentMethod}</h4>
      <h4>Status: {sale.status}</h4>
      {<h4>Created Date: {sale.createdAt}</h4>}
      
      <h2>Sale Items:</h2>
      {sale.items.map((item) => (
        <div key={item.productId}>
          <h3>{item.productName}</h3>
          <p>Quantity: {item.saleQuantity}</p>
          <p>Unit Price: {item.unitPrice}</p>
          <p>Line Total: {item.lineTotal}</p>
        </div>
      ))}

      <h4></h4>
    </div>
  );
}
