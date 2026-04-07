import React, { useState } from 'react';
import { CheckCircle } from 'lucide-react';
import {
  Card,
  CardHeader,
  CardContent,
  CardFooter,
} from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';

interface PlanSection {
  title: string;
  items: string[];
}

interface PlanMessageProps {
  sections: PlanSection[];
  onApprove: () => void;
  onRequestRevision: (feedback: string) => void;
  disabled?: boolean;
}

const PlanMessage: React.FC<PlanMessageProps> = ({
  sections,
  onApprove,
  onRequestRevision,
  disabled = false,
}) => {
  const [showFeedback, setShowFeedback] = useState(false);
  const [feedback, setFeedback] = useState('');
  const [isApproved, setIsApproved] = useState(false);

  const handleApprove = () => {
    setIsApproved(true);
    onApprove();
  };

  const handleRevisionToggle = () => {
    setShowFeedback(prev => !prev);
  };

  const handleSendFeedback = () => {
    if (!feedback.trim()) return;
    onRequestRevision(feedback.trim());
    setFeedback('');
    setShowFeedback(false);
  };

  const borderClass = isApproved
    ? 'border-green-500/60'
    : 'border-indigo-500/60';

  const gradientClass = isApproved
    ? 'bg-gradient-to-r from-green-500/10 to-emerald-500/10'
    : 'bg-gradient-to-r from-indigo-500/10 to-blue-500/10';

  return (
    <Card
      className={`bg-gray-900 text-gray-100 border-2 ${borderClass} ${gradientClass} max-w-2xl w-full`}
    >
      <CardHeader className="pb-3">
        <div className="flex items-center gap-2">
          <span className="text-base font-bold">
            {isApproved ? '✅ 구현 계획' : '📋 구현 계획'}
          </span>
          <Badge className="bg-indigo-600 text-white text-xs">
            {sections.length}개 섹션
          </Badge>
          {isApproved && (
            <Badge className="bg-green-600 text-white text-xs ml-auto">
              승인됨
            </Badge>
          )}
        </div>
      </CardHeader>

      <CardContent className="space-y-4 pt-0">
        {sections.map((section, sectionIdx) => (
          <div key={sectionIdx} className="space-y-2">
            <div className="flex items-center gap-2">
              <span className="rounded-full bg-indigo-500 w-6 h-6 flex items-center justify-center text-white text-xs font-bold shrink-0">
                {sectionIdx + 1}
              </span>
              <span className="font-semibold text-sm text-gray-100">
                {section.title}
              </span>
            </div>
            <ul className="space-y-1 pl-8">
              {section.items.map((item, itemIdx) => (
                <li key={itemIdx} className="flex items-start gap-2 text-sm text-gray-300">
                  <CheckCircle className="h-4 w-4 text-indigo-400 shrink-0 mt-0.5" />
                  <span>{item}</span>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </CardContent>

      <CardFooter className="flex flex-col items-stretch gap-3 pt-2">
        {!isApproved && (
          <div className="flex gap-2">
            <Button
              onClick={handleApprove}
              disabled={disabled || isApproved}
              className="bg-green-600 hover:bg-green-700 text-white flex-1"
              size="sm"
            >
              ✅ 승인
            </Button>
            <Button
              onClick={handleRevisionToggle}
              disabled={disabled || isApproved}
              variant="outline"
              className="border-gray-600 text-gray-300 hover:bg-gray-800 hover:text-gray-100 flex-1"
              size="sm"
            >
              ✏️ 수정 요청
            </Button>
          </div>
        )}

        {isApproved && (
          <div className="flex gap-2">
            <Button
              disabled
              className="bg-green-700 text-white flex-1 opacity-60 cursor-not-allowed"
              size="sm"
            >
              ✅ 승인됨
            </Button>
            <Button
              disabled
              variant="outline"
              className="border-gray-700 text-gray-500 flex-1 opacity-60 cursor-not-allowed"
              size="sm"
            >
              ✏️ 수정 요청
            </Button>
          </div>
        )}

        {showFeedback && !isApproved && (
          <div className="space-y-2">
            <Textarea
              value={feedback}
              onChange={e => setFeedback(e.target.value)}
              placeholder="수정 사항을 입력해주세요..."
              className="bg-gray-800 border-gray-600 text-gray-100 placeholder:text-gray-500 resize-none min-h-[80px]"
              disabled={disabled}
            />
            <Button
              onClick={handleSendFeedback}
              disabled={disabled || !feedback.trim()}
              size="sm"
              className="w-full bg-indigo-600 hover:bg-indigo-700 text-white"
            >
              전송
            </Button>
          </div>
        )}
      </CardFooter>
    </Card>
  );
};

export default PlanMessage;
