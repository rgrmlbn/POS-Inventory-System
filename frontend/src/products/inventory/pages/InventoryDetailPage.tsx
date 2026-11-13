// pages/ProductDetailPage.tsx
import { useParams, Link } from "react-router-dom";
import { useInventoryQuery } from "../hooks/useInventoryQuery";

export function InventoryDetailPage() {
  const { id } = useParams();
  const { product, loading, error } = useInventoryQuery(Number(id));

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load product.</p>;
  if (!product) return <p>Product not found.</p>;

  return (
    <div>
      <Link to="/inventories">← Back to inventories</Link>
      <h1>{product.productName}</h1>
      <p>
        <b>Quantity: </b>
        {product.quantity}
      </p>
    </div>
  );
}
