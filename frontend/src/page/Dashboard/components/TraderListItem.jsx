import {Link} from "react-router-dom";

export default function TraderListItem({ trader }) {
  return (
      <li className="trader-item">
        <div className="trader-item__info">
          <div className="trader-item__name">
            {trader.firstName} {trader.lastName}
          </div>
          <div className="trader-item__id">ID: {trader.id}</div>
        </div>

        <div className="trader-item__actions">
          <Link to={`/traders/${trader.id}`}>
            <button>View</button>
          </Link>
        </div>

      </li>
  );
}
