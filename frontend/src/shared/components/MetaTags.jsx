import { Helmet } from "react-helmet-async";

export default function MetaTags({ title, description, keywords, image, url }) {
  const defaultTitle = "Car Rental Marketplace - Find Your Perfect Ride";
  const defaultDescription =
    "Browse thousands of cars for rent. Find the perfect vehicle for your needs with our easy-to-use platform.";
  const defaultImage = "/og-image.jpg";
  const siteUrl = import.meta.env.VITE_SITE_URL || "http://localhost:5173";

  const pageTitle = title ? `${title} | Car Rental Marketplace` : defaultTitle;
  const pageDescription = description || defaultDescription;
  const pageImage = image || defaultImage;
  const pageUrl = url || siteUrl;

  return (
    <Helmet>
      {/* Basic Meta Tags */}
      <title>{pageTitle}</title>
      <meta name="description" content={pageDescription} />
      {keywords && <meta name="keywords" content={keywords} />}

      {/* Open Graph / Facebook */}
      <meta property="og:type" content="website" />
      <meta property="og:url" content={pageUrl} />
      <meta property="og:title" content={pageTitle} />
      <meta property="og:description" content={pageDescription} />
      <meta property="og:image" content={pageImage} />

      {/* Twitter */}
      <meta property="twitter:card" content="summary_large_image" />
      <meta property="twitter:url" content={pageUrl} />
      <meta property="twitter:title" content={pageTitle} />
      <meta property="twitter:description" content={pageDescription} />
      <meta property="twitter:image" content={pageImage} />

      {/* Canonical URL */}
      <link rel="canonical" href={pageUrl} />
    </Helmet>
  );
}
