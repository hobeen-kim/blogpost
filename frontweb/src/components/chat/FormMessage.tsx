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
  type: "radio" | "text";
  options?: string[];
  recommended?: string;
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
  const isSubmittable = radioFields.every((f) => !!formData[f.id]);

  const handleRadioChange = (fieldId: string, value: string) => {
    if (isDisabled) return;
    setFormData((prev) => ({ ...prev, [fieldId]: value }));
  };

  const handleTextChange = (fieldId: string, value: string) => {
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
