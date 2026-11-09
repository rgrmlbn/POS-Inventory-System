import { useState } from "react";
import { useCategoriesQuery } from "../hooks/useCategoriesQuery";
import { Pagination } from "../../../shared/components/Pagination";
import { CategoryList } from "../components/CategoryList";
import { CategorySearchForm } from "../components/CategorySearchForm";

export function CategoryPage() {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [categoryName, setCategoryName] = useState("");

  const { categories, totalPages, loading, error } = useCategoriesQuery(
    categoryName,
    page,
    size,
  );

  return (
    <div>
      <h1>Categories</h1>

      <CategorySearchForm
        onSearch={(name) => {
          setCategoryName(name);
          setPage(0);
        }}
        onClear={() => {
          setCategoryName("");
          setPage(0);
        }}
      />

      <CategoryList categories={categories} loading={loading} error={error} />

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
