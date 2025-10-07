import React, { useEffect, useState } from "react";
import "./App.css";

type Post = {
  postId: number;
  title: string;
  category: string;
  description: string;
  url: string;
  thumbnailUrl: string;
  createdBy: string;
  createdAt: string;
};

type ApiResponse = {
  content: Post[];
  page: number;
  size: number;
  totalCount: number;
};

const App: React.FC = () => {
  const [posts, setPosts] = useState<Post[]>([]);
  const [page, setPage] = useState(0);
  const [size] = useState(6);
  const [category, setCategory] = useState<string | undefined>();
  const [totalCount, setTotalCount] = useState(0);
  const [loading, setLoading] = useState(false);

  const totalPages = Math.ceil(totalCount / size);

  const fetchPosts = async () => {
    setLoading(true);
    const params = new URLSearchParams();
    params.append("page", page.toString());
    params.append("size", size.toString());
    if (category) params.append("category", category);

    const res = await fetch(`http://localhost:8080/api/v1/posts?${params.toString()}`);
    const data: ApiResponse = await res.json();
    setPosts(data.content);
    setTotalCount(data.totalCount);
    setLoading(false);
  };

  useEffect(() => {
    fetchPosts();
  }, [page, category]);

  return (
      <div className="app">
        <h1 className="header">Modern Blog</h1>

        <div className="filter">
          <select
              value={category || ""}
              onChange={(e) => {
                setCategory(e.target.value || undefined);
                setPage(0);
              }}
          >
            <option value="">전체 카테고리</option>
            <option value="Java">Java</option>
            <option value="Kubenetes">Kubenetes</option>
          </select>
        </div>

        {loading && <p className="loading">로딩 중...</p>}

        {!loading && (
            <>
              <div className="grid">
                {posts.map((post) => (
                    <a key={post.postId} href={post.url} className="card">
                      <img
                          src={post.thumbnailUrl}
                          alt={post.title}
                          className="thumbnail"
                      />
                      <div className="content">
                        <span className="category">{post.category}</span>
                        <h2 className="title">{post.title}</h2>
                        <p className="description">{post.description}</p>
                        <div className="meta">
                          <span>{post.createdBy}</span>
                          <span>{new Date(post.createdAt).toLocaleDateString()}</span>
                        </div>
                      </div>
                    </a>
                ))}
              </div>

              <div className="pagination">
                <button
                    disabled={page === 0}
                    onClick={() => setPage((p) => p - 1)}
                >
                  이전
                </button>
                <span>
              {page + 1} / {totalPages || 1}
            </span>
                <button
                    disabled={page + 1 >= totalPages}
                    onClick={() => setPage((p) => p + 1)}
                >
                  다음
                </button>
              </div>
            </>
        )}
      </div>
  );
};

export default App;
