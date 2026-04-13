import React, { useState, useRef, useEffect } from 'react';
import { Send, ArrowLeft, Sparkles, PanelLeftOpen, PanelLeftClose, ExternalLink } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { askQuestion } from '@/lib/api';
import { useAuth } from '@/contexts/AuthContext';
import FormMessage from '@/components/chat/FormMessage';
import PlanMessage from '@/components/chat/PlanMessage';
import ArchitectureMessage from '@/components/chat/ArchitectureMessage';

interface FormField {
  id: string;
  label: string;
  type: 'radio' | 'text' | 'checkbox' | 'select' | 'slider' | 'number';
  options?: string[];
  recommended?: string;
  min?: number;
  max?: number;
  step?: number;
}

interface PlanSection {
  title: string;
  items: string[];
}

interface ArchitectureComponent {
  name: string;
  description: string;
  tech?: string;
}

interface Source {
  title: string;
  url: string;
  source: string;
  thumbnail?: string;
}

interface Message {
  role: 'user' | 'assistant';
  content: string;
  sources?: Source[];
  widgetType?: 'form' | 'plan' | 'architecture';
  formFields?: FormField[];
  planSections?: PlanSection[];
  architectureDiagram?: string;
  architectureComponents?: ArchitectureComponent[];
}

const TypingDots = () => (
  <div className="flex items-center space-x-1 py-1">
    <span className="w-2 h-2 bg-gray-400 rounded-full animate-bounce [animation-delay:0ms]" />
    <span className="w-2 h-2 bg-gray-400 rounded-full animate-bounce [animation-delay:150ms]" />
    <span className="w-2 h-2 bg-gray-400 rounded-full animate-bounce [animation-delay:300ms]" />
  </div>
);

const SourceBadge: React.FC<{ index: number; source: Source }> = ({ index, source }) => (
  <button
    onClick={() => window.open(source.url, '_blank')}
    title={source.title}
    className="inline-flex items-center px-1.5 py-0.5 mx-0.5 text-[11px] font-medium bg-purple-600 text-white rounded cursor-pointer hover:bg-purple-700 transition-colors align-baseline"
  >
    {source.source}
  </button>
);

const renderMessageContent = (content: string, sources: Source[]) => {
  // Add line break after sentence-ending ". " if not already followed by newline
  let processed = content.replace(/\. (?!\n)(?!$)/g, '.\n');

  const parts = processed.split(/(\[\d+\])/g);
  return parts.map((part, i) => {
    const match = part.match(/^\[(\d+)\]$/);
    if (match) {
      const idx = parseInt(match[1], 10) - 1;
      if (idx >= 0 && idx < sources.length) {
        return <SourceBadge key={i} index={idx} source={sources[idx]} />;
      }
    }
    // Process bold and bullet syntax
    const lines = part.split('\n');
    const rendered = lines.map((line, li) => {
      const isBullet = line.startsWith('- ');
      const lineText = isBullet ? '• ' + line.slice(2) : line;
      const boldParts = lineText.split(/(\*\*[^*]+\*\*)/g);
      const nodes = boldParts.map((bp, bi) => {
        const boldMatch = bp.match(/^\*\*([^*]+)\*\*$/);
        if (boldMatch) return <strong key={bi}>{boldMatch[1]}</strong>;
        return <span key={bi}>{bp}</span>;
      });
      return (
        <span key={li}>
          {nodes}
          {li < lines.length - 1 && '\n'}
        </span>
      );
    });
    return <span key={i}>{rendered}</span>;
  });
};

const ChatPage: React.FC = () => {
  const navigate = useNavigate();
  const { user, signInWithGoogle } = useAuth();
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [allSources, setAllSources] = useState<Source[]>([]);
  const [latestSources, setLatestSources] = useState<Source[]>([]);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [originalQuestion, setOriginalQuestion] = useState('');
  const [lastFormData, setLastFormData] = useState<Record<string, string>>({});
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isLoading]);

  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
      textareaRef.current.style.height = Math.min(textareaRef.current.scrollHeight, 150) + 'px';
    }
  }, [input]);

  const processSSEStream = async (response: Response) => {
    if (!response.body) {
      throw new Error('No response body');
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = '';
    let assistantContent = '';
    let currentSources: Source[] = [];

    setMessages(prev => [...prev, { role: 'assistant', content: '', sources: [] }]);

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      buffer += decoder.decode(value, { stream: true });
      const lines = buffer.split('\n');
      buffer = lines.pop() ?? '';

      for (const line of lines) {
        if (!line.startsWith('data:')) continue;
        const raw = line.slice(5).trim();
        if (!raw) continue;

        try {
          const event = JSON.parse(raw);

          if (event.type === 'source') {
            currentSources = event.sources ?? [];
            setLatestSources(currentSources);
            setAllSources(prev => {
              const existingUrls = new Set(prev.map(s => s.url));
              const newSources = currentSources.filter(s => !existingUrls.has(s.url));
              return [...prev, ...newSources];
            });
          } else if (event.type === 'token') {
            assistantContent += event.content ?? '';
            setMessages(prev => {
              const updated = [...prev];
              updated[updated.length - 1] = {
                role: 'assistant',
                content: assistantContent,
                sources: currentSources,
              };
              return updated;
            });
          } else if (event.type === 'form') {
            setMessages(prev => {
              const updated = [...prev];
              updated[updated.length - 1] = {
                role: 'assistant',
                content: assistantContent,
                sources: currentSources,
                widgetType: 'form',
                formFields: event.fields,
              };
              return updated;
            });
          } else if (event.type === 'plan') {
            setMessages(prev => {
              const updated = [...prev];
              updated[updated.length - 1] = {
                ...updated[updated.length - 1],
                widgetType: 'plan',
                planSections: event.sections,
              };
              return updated;
            });
          } else if (event.type === 'architecture') {
            setMessages(prev => {
              const updated = [...prev];
              updated[updated.length - 1] = {
                ...updated[updated.length - 1],
                widgetType: 'architecture',
                architectureDiagram: event.diagram,
                architectureComponents: event.components,
              };
              return updated;
            });
          } else if (event.type === 'done') {
            setIsLoading(false);
          } else if (event.type === 'error') {
            setMessages(prev => {
              const updated = [...prev];
              updated[updated.length - 1] = {
                role: 'assistant',
                content: event.content ?? '오류가 발생했습니다.',
                sources: [],
              };
              return updated;
            });
            setIsLoading(false);
          }
        } catch {
          // Skip malformed JSON
        }
      }
    }
  };

  const handleSubmit = async () => {
    const question = input.trim();
    if (!question || isLoading) return;

    if (messages.length === 0) {
      setOriginalQuestion(question);
    }

    const userMessage: Message = { role: 'user', content: question };
    const history = messages.slice(-20).map(m => ({ role: m.role, content: m.content }));
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setIsLoading(true);
    setLatestSources([]);

    try {
      const response = await askQuestion({ question, history });
      await processSSEStream(response);
    } catch {
      setMessages(prev => {
        const last = prev[prev.length - 1];
        if (last?.role === 'assistant' && last.content === '') {
          const updated = [...prev];
          updated[updated.length - 1] = {
            role: 'assistant',
            content: '요청 중 오류가 발생했습니다.',
            sources: [],
          };
          return updated;
        }
        return [...prev, { role: 'assistant', content: '요청 중 오류가 발생했습니다.', sources: [] }];
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleFormSubmit = async (formData: Record<string, string>) => {
    setLastFormData(formData);
    const history = messages.slice(-20).map(m => ({ role: m.role, content: m.content }));
    setIsLoading(true);
    setMessages(prev => [...prev, { role: 'user', content: '정보를 제출했습니다.' }]);

    try {
      const response = await askQuestion({
        question: originalQuestion,
        history,
        step: 'plan',
        formData,
      });
      await processSSEStream(response);
    } catch {
      setMessages(prev => [
        ...prev,
        { role: 'assistant', content: '요청 중 오류가 발생했습니다.', sources: [] },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const handlePlanApprove = async () => {
    const history = messages.slice(-20).map(m => ({ role: m.role, content: m.content }));
    setIsLoading(true);
    setMessages(prev => [...prev, { role: 'user', content: '계획을 승인했습니다.' }]);

    try {
      const response = await askQuestion({
        question: originalQuestion,
        history,
        step: 'architecture',
        approval: true,
        formData: lastFormData,
      });
      await processSSEStream(response);
    } catch {
      setMessages(prev => [
        ...prev,
        { role: 'assistant', content: '요청 중 오류가 발생했습니다.', sources: [] },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const handlePlanRevision = async (feedback: string) => {
    const history = messages.slice(-20).map(m => ({ role: m.role, content: m.content }));
    setIsLoading(true);
    setMessages(prev => [...prev, { role: 'user', content: `수정 요청: ${feedback}` }]);

    try {
      const response = await askQuestion({
        question: originalQuestion,
        history,
        step: 'plan',
        approval: false,
        feedback,
        formData: lastFormData,
      });
      await processSSEStream(response);
    } catch {
      setMessages(prev => [
        ...prev,
        { role: 'assistant', content: '요청 중 오류가 발생했습니다.', sources: [] },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSubmit();
    }
  };

  if (!user) {
    return (
      <div className="h-screen flex items-center justify-center bg-background">
        <div className="text-center space-y-4">
          <Sparkles className="h-12 w-12 text-purple-500 mx-auto" />
          <h2 className="text-xl font-semibold">로그인이 필요합니다</h2>
          <p className="text-muted-foreground">AI 아키텍처 설계를 사용하려면 로그인해주세요.</p>
          <div className="flex gap-3 justify-center">
            <Button variant="outline" onClick={() => navigate('/')}>돌아가기</Button>
            <Button onClick={() => signInWithGoogle()} className="bg-gradient-to-r from-purple-600 to-indigo-600 text-white">
              Google 로그인
            </Button>
          </div>
        </div>
      </div>
    );
  }

  const Sidebar = () => (
    <div className="h-full flex flex-col">
      <div className="p-4 border-b font-semibold text-sm flex items-center justify-between">
        <span>참고 포스트</span>
        <button className="md:hidden" onClick={() => setSidebarOpen(false)}>
          <PanelLeftClose className="h-4 w-4" />
        </button>
      </div>
      <div className="flex-1 overflow-y-auto p-3 space-y-2">
        {allSources.length === 0 ? (
          <p className="text-sm text-muted-foreground text-center pt-8">
            질문을 하면 관련 포스트가 여기에 표시됩니다
          </p>
        ) : (
          allSources.map((s, idx) => (
            <a
              key={idx}
              href={s.url}
              target="_blank"
              rel="noopener noreferrer"
              className="block p-3 rounded-lg border hover:bg-muted/50 transition-colors group"
            >
              <div className="flex items-start gap-2">
                {s.thumbnail && (
                  <img src={s.thumbnail} alt="" className="w-10 h-10 rounded object-cover shrink-0" />
                )}
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-medium truncate group-hover:text-purple-600 transition-colors">
                    {s.title}
                  </p>
                  <span className="inline-flex items-center mt-1 px-2 py-0.5 text-[10px] font-medium bg-purple-100 text-purple-700 dark:bg-purple-900 dark:text-purple-300 rounded-full">
                    {s.source}
                  </span>
                </div>
                <ExternalLink className="h-3 w-3 text-muted-foreground shrink-0 mt-1 opacity-0 group-hover:opacity-100 transition-opacity" />
              </div>
            </a>
          ))
        )}
      </div>
    </div>
  );

  return (
    <div className="h-screen flex bg-background">
      {/* Desktop sidebar */}
      <div className="hidden md:block w-[300px] border-r shrink-0">
        <Sidebar />
      </div>

      {/* Mobile sidebar drawer */}
      {sidebarOpen && (
        <>
          <div className="fixed inset-0 bg-black/50 z-40 md:hidden" onClick={() => setSidebarOpen(false)} />
          <div className="fixed left-0 top-0 bottom-0 w-[300px] bg-background z-50 md:hidden border-r">
            <Sidebar />
          </div>
        </>
      )}

      {/* Chat area */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Header */}
        <div className="h-14 border-b flex items-center px-4 gap-3 shrink-0">
          <button className="md:hidden" onClick={() => setSidebarOpen(true)}>
            <PanelLeftOpen className="h-5 w-5 text-muted-foreground" />
          </button>
          <Button variant="ghost" size="sm" onClick={() => navigate('/')} className="gap-1 text-muted-foreground">
            <ArrowLeft className="h-4 w-4" />
            돌아가기
          </Button>
          <div className="flex items-center gap-2 ml-2">
            <Sparkles className="h-5 w-5 text-purple-500" />
            <h1 className="font-semibold">AI 아키텍처 챗봇</h1>
          </div>
        </div>

        {/* Messages */}
        <div className="flex-1 overflow-y-auto px-4 py-6">
          <div className="max-w-3xl mx-auto space-y-4">
            {messages.length === 0 && (
              <div className="text-center text-muted-foreground text-sm pt-20">
                <Sparkles className="h-10 w-10 text-purple-400 mx-auto mb-4" />
                <p className="text-lg font-medium mb-1">어떤 시스템을 설계할까요?</p>
                <p>질문을 입력하면 AI가 필요한 정보를 수집하고 아키텍처를 설계합니다.</p>
              </div>
            )}
            {messages.map((msg, idx) => (
              <div
                key={idx}
                className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                <div
                  className={`${msg.widgetType ? 'max-w-[90%]' : 'max-w-[80%]'} rounded-2xl px-4 py-3 text-sm whitespace-pre-wrap leading-relaxed ${
                    msg.role === 'user'
                      ? 'bg-purple-600 text-white'
                      : 'bg-gray-100 text-gray-900 dark:bg-gray-800 dark:text-gray-100'
                  }`}
                >
                  {msg.role === 'assistant' ? (
                    <>
                      {msg.content && renderMessageContent(msg.content, msg.sources ?? [])}
                      {msg.widgetType === 'form' && msg.formFields && (
                        <div className="mt-3">
                          <FormMessage
                            fields={msg.formFields}
                            onSubmit={handleFormSubmit}
                            disabled={isLoading}
                          />
                        </div>
                      )}
                      {msg.widgetType === 'plan' && msg.planSections && (
                        <div className="mt-3">
                          <PlanMessage
                            sections={msg.planSections}
                            onApprove={handlePlanApprove}
                            onRequestRevision={handlePlanRevision}
                            disabled={isLoading}
                          />
                        </div>
                      )}
                      {msg.widgetType === 'architecture' && (
                        <div className="mt-3">
                          <ArchitectureMessage
                            diagram={msg.architectureDiagram ?? ''}
                            components={msg.architectureComponents ?? []}
                          />
                        </div>
                      )}
                    </>
                  ) : (
                    msg.content
                  )}
                </div>
              </div>
            ))}
            {isLoading && messages[messages.length - 1]?.content === '' && (
              <div className="flex justify-start">
                <div className="bg-gray-100 dark:bg-gray-800 rounded-2xl px-4 py-3">
                  <TypingDots />
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>
        </div>

        {/* Input */}
        <div className="border-t p-4 shrink-0">
          <div className="max-w-3xl mx-auto flex items-end gap-2">
            <textarea
              ref={textareaRef}
              value={input}
              onChange={e => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="설계하고 싶은 시스템을 설명해주세요..."
              disabled={isLoading}
              rows={1}
              className="flex-1 resize-none rounded-xl border border-input bg-background px-4 py-3 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:opacity-50"
            />
            <Button
              onClick={handleSubmit}
              disabled={isLoading || !input.trim()}
              size="icon"
              className="h-11 w-11 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white shrink-0"
            >
              <Send className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChatPage;
