import { useState } from "react";

import { Pagination } from "../../../shared/components/Pagination";
import { PurchaseList } from "../components/PurchaseList";
import { usePurchasesPageQuery } from "../hooks/usePurchasesPageQuery"; 
import { PurchaseSearchForm } from "../components/PurchaseSearchForm";


export function PurchasePage() {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [productName, setProductName] = useState("");

  const { purchases, totalPages, loading, error } = usePurchasesPageQuery(
    productName,
    page,
    size,
  );

  return (
    <div>
      <h1>Purchases</h1>

      <PurchaseSearchForm
        onSearch={(name) => {
          setProductName(name);
          setPage(0);
        }}
        onClear={() => {
          setProductName("");
          setPage(0);
        }}
      />

      <PurchaseList purchases={purchases} loading={loading} error={error} />

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
