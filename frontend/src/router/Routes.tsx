import { Routes, Route } from "react-router-dom";
import { ProductPage } from "../products/products/pages/ProductPage";
import { ProductDetailPage } from "../products/products/pages/ProductDetailPage";
import { CategoryPage } from "../products/category/pages/CategoryPage";
import { InventoryPage } from "../products/inventory/pages/InventoryPage";
import { InventoryDetailPage } from "../products/inventory/pages/InventoryDetailPage";

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/products" element={<ProductPage />} />
      <Route path="/categories" element={<CategoryPage />} />
      <Route path="/products/:id" element={<ProductDetailPage />} />
      <Route path="/inventories" element={<InventoryPage />} />
      <Route path="/inventories/:id" element={<InventoryDetailPage />} />

      <Route path="*" element={<p>Page not found.</p>} />
    </Routes>
  );
}
