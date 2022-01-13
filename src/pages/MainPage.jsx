import { NavLink, Outlet } from 'react-router-dom';

export default function Main() {
  return (
    <main>
      <nav>
        <NavLink
          to="/users"
        >
          Users
        </NavLink>
        <NavLink
          to="/tags"
        >
          Tags
        </NavLink>
        <NavLink
          to="/login"
        >
          Login
        </NavLink>
        <NavLink
          to="/register"
        >
          register
        </NavLink>
      </nav>
      <Outlet />
    </main>
  )
}
