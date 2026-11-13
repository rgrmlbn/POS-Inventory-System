import { useState } from "react";

import { Pagination } from "../../../shared/components/Pagination";
import { InventoryList } from "../components/InventoryList";
import { useInventoriesQuery } from "../hooks/useInventoriesQuery";
import { InventorySearchForm } from "../components/InventorySearchForm";

export function InventoryPage() {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [productName, setProductName] = useState("");

  const { inventories, totalPages, loading, error } = useInventoriesQuery(
    productName,
    page,
    size,
  );

  return (
    <div>
      <h1>Categories</h1>

      <InventorySearchForm
        onSearch={(name) => {
          setProductName(name);
          setPage(0);
        }}
        onClear={() => {
          setProductName("");
          setPage(0);
        }}
      />

      <InventoryList inventories={inventories} loading={loading} error={error} />

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
