// pages/ProductDetailPage.tsx
import { useParams, Link } from "react-router-dom";
import { useProductQuery } from "../hooks/get/useProductQuery";
import type { Product } from "../types/Product";
import { ProductEditForm } from "../components/ProductEditForm";
import { ProductDeleteButton } from "../button/ProductDeleteButton";
import { useState } from "react";

function ProductItem({ product }: { product: Product }) {
  const [isEditing, setIsEditing] = useState(false);

  if (isEditing) {
    return (
      <ProductEditForm product={product} onDone={() => setIsEditing(false)} />
    );
  } else
    return (
      <div>
        <h1>{product.productName}</h1>
        <button type="button" onClick={() => setIsEditing(true)}>
          Edit
        </button>
        <ProductDeleteButton id={product.id} />
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

export function ProductDetailPage() {
  const { id } = useParams();
  const { product, loading, error } = useProductQuery(Number(id));

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load product.</p>;
  if (!product) return <p>Product not found.</p>;

  return (
    <div>
      <Link to="/products">← Back to products</Link>
      <ProductItem key={product.id} product={product} />
    </div>
  );
}
