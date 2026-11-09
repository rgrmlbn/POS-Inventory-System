// pages/ProductDetailPage.tsx
import { useParams, Link } from "react-router-dom";
import { useProductQuery } from "../hooks/useProductQuery";

export function ProductDetailPage() {
  const { id } = useParams();
  const { product, loading, error } = useProductQuery(Number(id));

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load product.</p>;
  if (!product) return <p>Product not found.</p>;

  return (
    <div>
      <Link to="/products">← Back to products</Link>
      <h1>{product.productName}</h1>
      <p>
        <b>Category: </b>
        {product.categoryName}
      </p>
      <p>
        <b>SKU: </b>
        {product.sku}
      </p>
      <p>
        <b>Barcode: </b>
        {product.barcode}
      </p>
      <ul>
        <li>Cost Price: {product.costPrice}</li>
        <li>Selling Price: {product.sellingPrice}</li>
        <li>Reorder Level: {product.reorderLevel}</li>
        <li>Available Quantity: {product.availableQuantity}</li>
      </ul>
    </div>
  );
}
