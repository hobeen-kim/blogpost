import React from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { cn } from '@/lib/utils';

const AdSpace: React.FC = () => {
  return (
    <Card className={cn(
      // "w-full max-w-sm md:max-w-lg lg:max-w-2xl mx-auto",
      // "border-2 border-dashed border-muted-foreground/30",
      // "bg-muted/20 hover:bg-muted/30 transition-colors duration-200"
    )}>
      <CardContent className="flex flex-col items-center justify-center p-8 md:p-12 lg:p-16">
        {/*<div className="text-center space-y-3">*/}
        {/*  <div className="w-16 h-16 md:w-20 md:h-20 mx-auto bg-muted-foreground/10 rounded-lg flex items-center justify-center">*/}
        {/*    <div className="w-8 h-8 md:w-10 md:h-10 bg-muted-foreground/20 rounded"></div>*/}
        {/*  </div>*/}
        {/*  <h3 className="text-lg md:text-xl font-medium text-muted-foreground">*/}
        {/*    /!*광고 영역*!/*/}
        {/*  </h3>*/}
        {/*  <p className="text-sm md:text-base text-muted-foreground/70 max-w-xs">*/}
        {/*    /!*이곳에 광고가 표시됩니다*!/*/}
        {/*  </p>*/}
        {/*</div>*/}
      </CardContent>
    </Card>
  );
};

export default AdSpace;