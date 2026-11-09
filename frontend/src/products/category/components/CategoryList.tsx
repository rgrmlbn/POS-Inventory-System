import type { Category } from "../types/Category";

interface CategoryListProps {
  categories: Category[];
  loading: boolean;
  error: unknown;
}

export function CategoryList({ categories, loading, error }: CategoryListProps) {
  if (loading) return <p>Loading...</p>;
  if (error) return <p>Failed to load categories.</p>;
  if (categories.length === 0) return <p>No categories found.</p>;

  return (
    <>
      {categories.map((category) => (
        <div key={category.id}>
          <h2>{category.categoryName}</h2>
        </div>
      ))}
    </>
  );
}
