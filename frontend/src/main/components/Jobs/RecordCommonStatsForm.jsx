import { Button, Form } from "react-bootstrap";
import { useForm } from "react-hook-form";

function RecordCommonStatsForm({ submitAction }) {
  const { handleSubmit } = useForm();

  return (
    <Form onSubmit={handleSubmit(submitAction)}>
      <p>Click this button to record stats for all commons!</p>
      <Button type="submit" data-testid="RecordCommonStatsForm-Submit-Button">
        Generate
      </Button>
    </Form>
  );
}
export default RecordCommonStatsForm;
