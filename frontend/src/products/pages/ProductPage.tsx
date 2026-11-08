import { useState } from "react";
import { useProductsQuery } from "../hooks/useProductsQuery";
import { ProductList } from "../components/ProductList";
import { ProductSearchForm } from "../components/ProductSearchForm";
import { Pagination } from "../../shared/components/Pagination";

export function ProductPage() {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [productName, setProductName] = useState("");

  const { products, totalPages, loading, error } = useProductsQuery(
    productName,
    page,
    size,
  );

  return (
    <div>
      <h1>Products</h1>

      <ProductSearchForm
        onSearch={(name) => {
          setProductName(name);
          setPage(0);
        }}
        onClear={() => {
          setProductName("");
          setPage(0);
        }}
      />

      <ProductList products={products} loading={loading} error={error} />

      <Pagination
        page={page}
        totalPages={totalPages}
        size={size}
        onPageChange={setPage}
        onSizeChange={(nextSize) => {
          setSize(nextSize);
          setPage(0);
        }}
      />
    </div>
  );
}
