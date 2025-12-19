import { AuthProvider } from '@/contexts/AuthContext';
import { ThemeProvider } from '@/contexts/ThemeContext';
import Header from '@/components/Header';
import PostGrid from '@/components/PostGrid';
import AdSpace from '@/components/AdSpace';

const Index = () => {
  return (
    <ThemeProvider>
      <AuthProvider>
        <div className="min-h-screen bg-background">
          <Header />
          <main className="container mx-auto px-4 py-8">
            <div className="flex gap-8">
              {/* Left Ad Space */}
              <div className="hidden xl:block w-64 flex-shrink-0">
                <AdSpace position="left" />
              </div>
              
              {/* Main Content */}
              <div className="flex-1">
                <PostGrid />
              </div>
              
              {/* Right Ad Space */}
              <div className="hidden xl:block w-64 flex-shrink-0">
                <AdSpace position="right" />
              </div>
            </div>
          </main>
        </div>
      </AuthProvider>
    </ThemeProvider>
  );
};

export default Index;
