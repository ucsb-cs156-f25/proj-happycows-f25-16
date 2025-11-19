import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import RecordCommonStatsForm from "main/components/Jobs/RecordCommonStatsForm";
import { vi } from "vitest";

const mockedNavigate = vi.fn();

vi.mock("react-router", async () => ({
  ...(await vi.importActual("react-router")),
  useNavigate: () => mockedNavigate,
}));

describe("RecordCommonStatsForm tests", () => {
  it("button generates correctly", async () => {
    const submitAction = vi.fn();
    render(<RecordCommonStatsForm submitAction={submitAction} />);
    expect(
      screen.getByText("Click this button to record stats for all commons!")
    ).toBeInTheDocument();

    const submitButton = screen.getByTestId("RecordCommonStats-Submit-Button");
    expect(submitButton).toBeInTheDocument();
    expect(submitButton).toHaveTextContent("Generate");
  });

  it("calls submitAction when the button is clicked", async () => {
    const submitAction = vi.fn();
    render(<RecordCommonStatsForm submitAction={submitAction} />);

    const submitButton = screen.getByTestId("RecordCommonStats-Submit-Button");
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(submitAction).toHaveBeenCalledTimes(1);
    });
  });
});
