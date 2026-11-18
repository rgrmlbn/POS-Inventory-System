// components/CategoryList.tsx
import { useState } from "react";
import type { Category } from "../types/Category";
import { CategoryEditForm } from "./CategoryEditForm";
import { CategoryDeleteButton } from "../button/CategoryDeleteButton";

interface CategoryListProps {
  categories: Category[];
  loading: boolean;
  error: unknown;
}

function CategoryItem({ category }: { category: Category }) {
  const [isEditing, setIsEditing] = useState(false);

  if (isEditing) {
    return (
      <CategoryEditForm
        category={category}
        onDone={() => setIsEditing(false)}
      />
    );
  }

  return (
    <div>
      <h2>{category.categoryName}</h2>
      <button onClick={() => setIsEditing(true)}>Edit</button>
      <CategoryDeleteButton id={category.id} /> {/* new */}
    </div>
  );
}

export function CategoryList({
  categories,
  loading,
  error,
}: CategoryListProps) {
  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load categories.</p>;
  if (categories.length === 0) return <p>No categories found.</p>;

  return (
    <>
      {categories.map((category) => (
        <CategoryItem key={category.id} category={category} />
      ))}
    </>
  );
}
