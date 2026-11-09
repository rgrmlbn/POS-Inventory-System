import { Routes, Route } from "react-router-dom";
import { ProductPage } from "../products/products/pages/ProductPage";
import { ProductDetailPage } from "../products/products/pages/ProductDetailPage";
import { CategoryPage } from "../products/category/pages/CategoryPage";

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/products" element={<ProductPage />} />
      <Route path="/categories" element={<CategoryPage />} />
      <Route path="/products/:id" element={<ProductDetailPage />} />
      <Route path="*" element={<p>Page not found.</p>} />
    </Routes>
  );
}
