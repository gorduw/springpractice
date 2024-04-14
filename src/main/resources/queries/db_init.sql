USE [springpractise_db]
GO

/****** Object:  Table [dbo].[advisor]    Script Date: 09.04.2024 23:20:06 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[advisor](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[age] [int] NOT NULL,
	[name] [varchar](255) NOT NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO;

USE [springpractise_db]
GO

/****** Object:  Table [dbo].[asset_holding]    Script Date: 09.04.2024 23:20:17 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[asset_holding](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](100) NULL,
	[code] [varchar](20) NULL,
	[price] [decimal](10, 2) NULL,
 CONSTRAINT [PK__assethol__3214EC27E8418D99] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO;

USE [springpractise_db]
GO

/****** Object:  Table [dbo].[portfolio]    Script Date: 09.04.2024 23:20:26 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[portfolio](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](100) NULL,
	[time_range] [varchar](20) NULL,
	[risk_profile] [varchar](1) NULL,
	[fk_advisor_id] [bigint] NOT NULL,
 CONSTRAINT [PK__Portfoli__3213E83FA3F98F6E] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[portfolio]  WITH CHECK ADD  CONSTRAINT [FK_Portfolio_Advisor] FOREIGN KEY([fk_advisor_id])
REFERENCES [dbo].[advisor] ([id])
GO

ALTER TABLE [dbo].[portfolio] CHECK CONSTRAINT [FK_Portfolio_Advisor]
GO;

USE [springpractise_db]
GO

/****** Object:  Table [dbo].[portfolio_asset_xref]    Script Date: 09.04.2024 23:20:35 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[portfolio_asset_xref](
	[portfolio_id] [bigint] NOT NULL,
	[asset_id] [bigint] NOT NULL
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[portfolio_asset_xref]  WITH CHECK ADD  CONSTRAINT [FK__portfolio__asset__2B3F6F97] FOREIGN KEY([asset_id])
REFERENCES [dbo].[asset_holding] ([id])
GO

ALTER TABLE [dbo].[portfolio_asset_xref] CHECK CONSTRAINT [FK__portfolio__asset__2B3F6F97]
GO

ALTER TABLE [dbo].[portfolio_asset_xref]  WITH CHECK ADD  CONSTRAINT [FK__portfolio__portf__2A4B4B5E] FOREIGN KEY([portfolio_id])
REFERENCES [dbo].[portfolio] ([id])
GO

ALTER TABLE [dbo].[portfolio_asset_xref] CHECK CONSTRAINT [FK__portfolio__portf__2A4B4B5E]
GO;