import './NavBar.scss'
import { NavLink } from 'react-router-dom'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {
  faAddressBook as dashboardIcon,
  faMoneyBill as quoteIcon,
} from "@fortawesome/free-solid-svg-icons";


export default function NavBar() {
  return (
      <nav className="page-navigation">
        <NavLink to="/dashboard" className="page-navigation-header" />

        <NavLink to="/dashboard" className="page-navigation-item">
          <FontAwesomeIcon icon={dashboardIcon} />

          <NavLink to="/quotes" className="page-navigation-item">
            <FontAwesomeIcon icon={quoteIcon} />
          </NavLink>

        </NavLink>
      </nav>
  )
}
