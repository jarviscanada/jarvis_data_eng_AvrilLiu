import {useState} from "react";
import axios from "axios";
import {Button, DatePicker, Form, Input, Modal} from "antd";
import {tradersUrl} from "../../../util/constants";

export default function AddTraderButton({onTraderAdded}) {
  const [open, setOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm();

  const handleOpen = () => setOpen(true);

  const handleCancel = () => {
    setOpen(false);
    form.resetFields();
  };

  const handleFinish = async (values) => {
    try {
      setSubmitting(true);

      const payload = {
        firstName: values.firstName.trim(),
        lastName: values.lastName.trim(),
        email: values.email.trim(),
        dob: values.dob.format("YYYY-MM-DD"),
        country: values.country.trim(),
      };

      await axios.post(tradersUrl, payload);

      setOpen(false);
      form.resetFields();

      if (onTraderAdded) {
        onTraderAdded();
      }
    } catch (err) {
      console.error("Failed to create trader", err);
      Modal.error(
          {title: "Create trader failed", content: "Please try again."});
    } finally {
      setSubmitting(false);
    }
  };

  return (
      <div style={{marginBottom: 16}}>
        <Button type="primary" onClick={handleOpen}>
          + Add Trader
        </Button>

        <Modal
            title="Add Trader"
            open={open}
            onCancel={handleCancel}
            okText="Submit"
            onOk={() => form.submit()}
            confirmLoading={submitting}
            destroyOnClose
        >
          <Form
              form={form}
              layout="vertical"
              onFinish={handleFinish}
              initialValues={{
                firstName: "",
                lastName: "",
                email: "",
                dob: null,
                country: "",
              }}
          >
            <Form.Item
                label="First Name"
                name="firstName"
                rules={[
                  {required: true, message: "Please enter first name"},
                  {whitespace: true, message: "First name cannot be empty."}
                ]}
            >
              <Input placeholder="e.g. Avril"/>
            </Form.Item>

            <Form.Item
                label="Last Name"
                name="lastName"
                rules={[{required: true, message: "Please enter last name"},
                  {whitespace: true, message: "Last name cannot be empty."}
                ]}
            >
              <Input placeholder="e.g. Liu"/>
            </Form.Item>

            <Form.Item
                label="Email"
                name="email"
                rules={[
                  {required: true, message: "Please enter email"},
                  {type: "email", message: "Please enter a valid email"},
                ]}
            >
              <Input placeholder="e.g. avril@example.com"/>
            </Form.Item>

            <Form.Item
                label="Date of Birth"
                name="dob"
                rules={[{
                  required: true,
                  message: "Please select date of birth"
                }]}
            >
              <DatePicker style={{width: "100%"}}/>
            </Form.Item>

            <Form.Item
                label="Country"
                name="country"
                rules={[{required: true, message: "Please enter country"}]}
            >
              <Input placeholder="e.g. Canada"/>
            </Form.Item>
          </Form>
        </Modal>
      </div>
  );
}
