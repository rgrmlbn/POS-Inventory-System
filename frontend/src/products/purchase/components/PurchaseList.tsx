import { Link } from "react-router-dom";

import type { Purchase } from "../types/Purchase";

interface PurchaseListProps {
  purchases: Purchase[];
  loading: boolean;
  error: unknown;
}

export function PurchaseList({ purchases, loading, error }: PurchaseListProps) {
  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load purchases.</p>;
  if (purchases.length === 0) return <p>No purchases found.</p>;

  return (
    <>
      {purchases.map((purchase) => (
        <div key={purchase.id}>
          <h2>Purchase Lists:</h2>
          <h3>Product: {purchase.productName}</h3>
          <h4>Quantity: {purchase.purchaseQuantity}</h4>
          <h4>Supplier: {purchase.supplierName}</h4>
          <Link to={`/purchases/${purchase.id}`}>View details.</Link>
        </div>
      ))}
    </>
  );
}
