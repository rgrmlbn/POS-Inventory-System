import './App.css'
import { AuthProvider } from "./auth/AuthProvider";
import { AuthActions } from "./auth/components/AuthActions";
import { AppRoutes } from "./router/Routes"

function App() {
  return (
    <AuthProvider>
      <AuthActions />
      <AppRoutes />
    </AuthProvider>
  )
}

export default App