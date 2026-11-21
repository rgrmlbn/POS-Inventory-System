import { useState } from "react";

import { Pagination } from "../../../shared/components/Pagination";
import { SaleList } from "../components/SaleList";
import { useSalesPageQuery } from "../hooks/useSalesPageQuery"; 
import { SaleSearchForm } from "../components/SaleSearchForm";


export function SalePage() {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [productName, setProductName] = useState("");

  const { sales, totalPages, loading, error } = useSalesPageQuery(
    productName,
    page,
    size,
  );

  return (
    <div>
      <h1>Sales</h1>

      <SaleSearchForm
        onSearch={(name) => {
          setProductName(name);
          setPage(0);
        }}
        onClear={() => {
          setProductName("");
          setPage(0);
        }}
      />

      <SaleList sales={sales} loading={loading} error={error} />

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
