import { Table } from "antd";
import "antd/dist/antd.css";
import "./TraderList.scss";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTrashAlt } from "@fortawesome/free-solid-svg-icons";

import { useEffect, useState } from "react";
import axios from "axios";
import { tradersUrl, deleteTraderUrl } from "../../../util/constants";

export default function TraderList({ refreshKey }) {
  const [dataSource, setDataSource] = useState([]);

  const getTraders = async () => {
    const response = await axios.get(tradersUrl);
    setDataSource(response.data || []);
  };

  useEffect(() => {
    getTraders();
  }, [refreshKey]);

  const deleteTrader = async (id) => {
    await axios.delete(`${deleteTraderUrl}/${id}`);
    await getTraders();
  };

  const columns = [
    { title: "First Name", dataIndex: "firstName", key: "firstName" },
    { title: "Last Name", dataIndex: "lastName", key: "lastName" },
    { title: "Email", dataIndex: "email", key: "email" },
    { title: "Date of Birth", dataIndex: "dob", key: "dob" },
    { title: "Country", dataIndex: "country", key: "country" },
    {
      title: "Actions",
      key: "actions",
      render: (_, record) => (
          <span
              className="delete-hitbox"
              onClick={(e) => {
                e.preventDefault();
                e.stopPropagation();
                deleteTrader(record.id);
              }}
          >
          <FontAwesomeIcon icon={faTrashAlt} />
        </span>
      ),
    },
  ];

  return (
      <Table
          rowKey="id"
          dataSource={dataSource}
          columns={columns}
          pagination={false}
      />
  );
}
