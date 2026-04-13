import React, { useState } from "react";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

interface FormField {
  id: string;
  label: string;
  type: "radio" | "text" | "checkbox" | "select" | "slider" | "number";
  options?: string[];
  recommended?: string;
  min?: number;
  max?: number;
  step?: number;
}

interface FormMessageProps {
  fields: FormField[];
  onSubmit: (formData: Record<string, string>) => void;
  disabled?: boolean;
}

const FormMessage: React.FC<FormMessageProps> = ({ fields, onSubmit, disabled = false }) => {
  const initialData: Record<string, string> = {};
  for (const field of fields) {
    if (field.recommended) {
      initialData[field.id] = field.recommended;
    } else {
      initialData[field.id] = "";
    }
  }

  const [formData, setFormData] = useState<Record<string, string>>(initialData);
  const [submitted, setSubmitted] = useState(false);

  const isDisabled = disabled || submitted;

  const radioFields = fields.filter((f) => f.type === "radio");
  const selectFields = fields.filter((f) => f.type === "select");
  const isSubmittable =
    radioFields.every((f) => !!formData[f.id]) &&
    selectFields.every((f) => !!formData[f.id]);

  const handleRadioChange = (fieldId: string, value: string) => {
    if (isDisabled) return;
    setFormData((prev) => ({ ...prev, [fieldId]: value }));
  };

  const handleTextChange = (fieldId: string, value: string) => {
    if (isDisabled) return;
    setFormData((prev) => ({ ...prev, [fieldId]: value }));
  };

  const handleCheckboxChange = (fieldId: string, option: string, checked: boolean) => {
    if (isDisabled) return;
    setFormData((prev) => {
      const current = prev[fieldId] ? prev[fieldId].split(",").filter(Boolean) : [];
      const next = checked ? [...current, option] : current.filter((v) => v !== option);
      return { ...prev, [fieldId]: next.join(",") };
    });
  };

  const handleSelectChange = (fieldId: string, value: string) => {
    if (isDisabled) return;
    setFormData((prev) => ({ ...prev, [fieldId]: value }));
  };

  const handleSliderChange = (fieldId: string, value: string) => {
    if (isDisabled) return;
    setFormData((prev) => ({ ...prev, [fieldId]: value }));
  };

  const handleNumberChange = (fieldId: string, value: string) => {
    if (isDisabled) return;
    setFormData((prev) => ({ ...prev, [fieldId]: value }));
  };

  const handleSubmit = () => {
    if (!isSubmittable || isDisabled) return;
    setSubmitted(true);
    onSubmit(formData);
  };

  return (
    <Card
      className={cn(
        "border border-purple-500/30 bg-purple-500/10 text-card-foreground",
        isDisabled && "opacity-60"
      )}
    >
      <CardHeader className="pb-3">
        <CardTitle className="text-sm font-semibold text-purple-300">
          📋 추가 정보가 필요합니다
        </CardTitle>
      </CardHeader>

      <CardContent className="space-y-5">
        {fields.map((field) => (
          <div key={field.id} className="space-y-2">
            <Label className="text-sm font-medium text-gray-200">{field.label}</Label>

            {field.type === "radio" && field.options && (
              <RadioGroup
                value={formData[field.id] ?? ""}
                onValueChange={(value) => handleRadioChange(field.id, value)}
                disabled={isDisabled}
                className="flex flex-col gap-2"
              >
                {field.options.map((option) => (
                  <div key={option} className="flex items-center gap-2">
                    <RadioGroupItem
                      value={option}
                      id={`${field.id}-${option}`}
                      className="border-purple-400 text-purple-400"
                    />
                    <Label
                      htmlFor={`${field.id}-${option}`}
                      className="text-sm text-gray-300 cursor-pointer"
                    >
                      {option}
                    </Label>
                    {field.recommended === option && (
                      <Badge className="text-[10px] px-1.5 py-0 h-4 bg-purple-600 text-white border-transparent hover:bg-purple-600">
                        추천
                      </Badge>
                    )}
                  </div>
                ))}
              </RadioGroup>
            )}

            {field.type === "text" && (
              <Textarea
                value={formData[field.id] ?? ""}
                onChange={(e) => handleTextChange(field.id, e.target.value)}
                disabled={isDisabled}
                placeholder="자유롭게 입력하세요..."
                className="bg-gray-900/50 border-gray-700 text-gray-200 placeholder:text-gray-500 focus-visible:ring-purple-500 min-h-[80px]"
              />
            )}

            {field.type === "checkbox" && field.options && (
              <div className="flex flex-col gap-2">
                {field.options.map((option) => {
                  const checked = (formData[field.id] ?? "")
                    .split(",")
                    .filter(Boolean)
                    .includes(option);
                  return (
                    <div key={option} className="flex items-center gap-2">
                      <input
                        type="checkbox"
                        id={`${field.id}-${option}`}
                        checked={checked}
                        disabled={isDisabled}
                        onChange={(e) => handleCheckboxChange(field.id, option, e.target.checked)}
                        className="accent-purple-500 w-4 h-4 cursor-pointer"
                      />
                      <Label
                        htmlFor={`${field.id}-${option}`}
                        className="text-sm text-gray-300 cursor-pointer"
                      >
                        {option}
                      </Label>
                      {field.recommended &&
                        field.recommended.split(",").map((r) => r.trim()).includes(option) && (
                          <Badge className="text-[10px] px-1.5 py-0 h-4 bg-purple-600 text-white border-transparent hover:bg-purple-600">
                            추천
                          </Badge>
                        )}
                    </div>
                  );
                })}
              </div>
            )}

            {field.type === "select" && field.options && (
              <select
                value={formData[field.id] ?? ""}
                onChange={(e) => handleSelectChange(field.id, e.target.value)}
                disabled={isDisabled}
                className="w-full bg-gray-700 text-white border border-gray-600 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500"
              >
                <option value="" disabled>
                  선택하세요
                </option>
                {field.options.map((option) => (
                  <option key={option} value={option}>
                    {option}
                    {field.recommended === option ? " (추천)" : ""}
                  </option>
                ))}
              </select>
            )}

            {field.type === "slider" && (
              <div className="flex items-center gap-3">
                <input
                  type="range"
                  min={field.min ?? 0}
                  max={field.max ?? 100}
                  step={field.step ?? 1}
                  value={formData[field.id] ?? field.recommended ?? String(field.min ?? 0)}
                  onChange={(e) => handleSliderChange(field.id, e.target.value)}
                  disabled={isDisabled}
                  className="flex-1 accent-purple-500"
                />
                <span className="text-sm text-gray-300 w-10 text-right">
                  {formData[field.id] ?? field.recommended ?? String(field.min ?? 0)}
                </span>
              </div>
            )}

            {field.type === "number" && (
              <input
                type="number"
                min={field.min}
                max={field.max}
                step={field.step}
                value={formData[field.id] ?? ""}
                onChange={(e) => handleNumberChange(field.id, e.target.value)}
                disabled={isDisabled}
                className="w-full bg-gray-700 text-white border border-gray-600 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500"
              />
            )}
          </div>
        ))}
      </CardContent>

      <CardFooter>
        <Button
          onClick={handleSubmit}
          disabled={!isSubmittable || isDisabled}
          className="bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white disabled:opacity-50"
        >
          제출
        </Button>
      </CardFooter>
    </Card>
  );
};

export default FormMessage;
