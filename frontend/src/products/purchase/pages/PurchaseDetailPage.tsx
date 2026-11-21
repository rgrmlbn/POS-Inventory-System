// pages/ProductDetailPage.tsx
import { useParams, Link } from "react-router-dom";
import { usePurchaseQuery } from "../hooks/get/usePurchaseQuery";

export function PurchaseDetailPage() {
  const { id } = useParams();
  const { purchase, loading, error } = usePurchaseQuery(Number(id));

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load purchase.</p>;
  if (!purchase) return <p>Purchase not found.</p>;

  return (
    <div>
      <Link to="/purchases">← Back to purchases</Link>
      <h3>Product: {purchase.productName}</h3>
      <h4>Quantity: {purchase.purchaseQuantity}</h4>
      <h4>Unit Cost: {purchase.unitCost}</h4>
      <h4>Total Cost: {purchase.totalCost}</h4>
      <h4>Supplier: {purchase.supplierName}</h4>
      <h4>Note: {purchase.note}</h4>
      <h4></h4>
    </div>
  );
}
