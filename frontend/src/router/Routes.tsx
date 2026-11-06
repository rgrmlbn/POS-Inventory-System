import { Routes, Route } from 'react-router-dom'
import { ProductPage } from '../products/pages/ProductPage'
import { ProductDetailPage } from '../products/pages/ProductDetailPage'

export function AppRoutes() {
    return (
        <Routes>
            <Route path="/products" element={<ProductPage />} />
            <Route path="/products/:id" element={<ProductDetailPage />} />
            <Route path="*" element={<p>Page not found.</p>} />
        </Routes>
    )
}