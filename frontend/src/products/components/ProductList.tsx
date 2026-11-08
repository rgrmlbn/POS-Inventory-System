import type { Product } from "../types";
import { Link } from "react-router-dom";

interface ProductListProps {
  products: Product[];
  loading: boolean;
  error: unknown;
}

export function ProductList({ products, loading, error }: ProductListProps) {
  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load products.</p>;
  if (products.length === 0) return <p>No products found.</p>;

  return (
    <>
      {products.map((product) => (
        <div key={product.id}>
          <h2>{product.productName}</h2>
          <p>
            <b>Category: </b>
            {product.categoryName}
          </p>
          <ul>
            <li>Reorder Level: {product.reorderLevel}</li>
            <li>Selling Price: {product.sellingPrice}</li>
            <li>Available Quantity: {product.availableQuantity}</li>
          </ul>
          <Link to={`/products/${product.id}`}>View details</Link>
        </div>
      ))}
    </>
  );
}
