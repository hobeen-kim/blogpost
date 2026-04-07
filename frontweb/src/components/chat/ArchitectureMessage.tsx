import mermaid from 'mermaid';
import { useEffect, useRef, useState } from 'react';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

interface ArchitectureComponent {
  name: string;
  description: string;
  tech?: string;
}

interface ArchitectureMessageProps {
  diagram: string;
  components: ArchitectureComponent[];
}

export default function ArchitectureMessage({ diagram, components }: ArchitectureMessageProps) {
  const diagramRef = useRef<HTMLDivElement>(null);
  const [renderError, setRenderError] = useState(false);

  useEffect(() => {
    mermaid.initialize({
      startOnLoad: false,
      theme: 'dark',
      themeVariables: {
        primaryColor: '#10b981',
        primaryTextColor: '#fff',
        primaryBorderColor: '#059669',
        lineColor: '#6ee7b7',
        secondaryColor: '#064e3b',
        tertiaryColor: '#022c22',
      },
    });

    const renderDiagram = async () => {
      try {
        const { svg } = await mermaid.render('arch-diagram-' + Date.now(), diagram);
        if (diagramRef.current) {
          diagramRef.current.innerHTML = svg;
        }
      } catch (e) {
        setRenderError(true);
      }
    };

    renderDiagram();
  }, [diagram]);

  return (
    <Card className="border bg-gray-900/80 text-gray-100" style={{ borderImage: 'linear-gradient(to right, #10b981, #059669) 1' }}>
      {/* Section 1: Architecture Diagram */}
      <CardHeader className="pb-3">
        <CardTitle className="text-lg text-emerald-400">🏗️ 아키텍처 설계</CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="bg-gray-900/50 rounded-lg p-4 overflow-x-auto">
          {renderError ? (
            <div className="space-y-2">
              <p className="text-xs text-red-400">다이어그램 렌더링 실패</p>
              <pre className="text-xs text-gray-300 whitespace-pre-wrap">
                <code>{diagram}</code>
              </pre>
            </div>
          ) : (
            <div ref={diagramRef} />
          )}
        </div>

        {/* Section 2: Component Details */}
        {components.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {components.map((component, index) => (
              <div
                key={index}
                className="rounded-lg border border-emerald-900/50 bg-gray-800/50 p-3 space-y-1"
              >
                <p className="font-bold text-gray-100">{component.name}</p>
                <p className="text-sm text-gray-400">{component.description}</p>
                {component.tech && (
                  <Badge className="bg-emerald-900/60 text-emerald-300 border border-emerald-700 text-xs hover:bg-emerald-900/80">
                    {component.tech}
                  </Badge>
                )}
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
}
