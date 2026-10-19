using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using QAClothingFactory.API.Data;
using QAClothingFactory.API.Models;

namespace QAClothingFactory.API.Controllers
{
    [ApiController]
    [Route("api/inventory")]
    [Authorize(Roles = "InventoryClerk,Admin")]
    public class InventoryController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public InventoryController(ApplicationDbContext context)
        {
            _context = context;
        }

        // ============================================================
        // DASHBOARD
        // GET: /api/inventory/dashboard
        // ============================================================

        [HttpGet("dashboard")]
        public async Task<IActionResult> GetDashboard()
        {
            var rawMaterials = await _context.RawMaterials
                .AsNoTracking()
                .ToListAsync();

            var finishedGoods = await _context.FinishedGoods
                .AsNoTracking()
                .ToListAsync();

            var transactionCount = await _context.InventoryTransactions
                .AsNoTracking()
                .CountAsync();

            var lowStockMaterials = rawMaterials
                .Count(m => m.CurrentStock <= m.ReorderLevel);

            var outOfStockMaterials = rawMaterials
                .Count(m => m.CurrentStock <= 0);

            var rawMaterialValue = rawMaterials
                .Sum(m => m.CurrentStock * m.UnitCost);

            var finishedGoodsValue = finishedGoods
                .Sum(p => p.CurrentStock * p.UnitPrice);

            return Ok(new
            {
                success = true,
                dashboard = new
                {
                    totalRawMaterials = rawMaterials.Count,
                    lowStockMaterials,
                    outOfStockMaterials,
                    totalFinishedGoods = finishedGoods.Count,
                    totalTransactions = transactionCount,
                    rawMaterialStockValue = rawMaterialValue,
                    finishedGoodsStockValue = finishedGoodsValue,
                    totalInventoryValue = rawMaterialValue + finishedGoodsValue
                }
            });
        }

        // ============================================================
        // ALL INVENTORY STOCK
        // GET: /api/inventory/stock
        // ============================================================

        [HttpGet("stock")]
        public async Task<IActionResult> GetInventoryStock()
        {
            var rawMaterials = await _context.RawMaterials
                .AsNoTracking()
                .OrderBy(m => m.MaterialName)
                .Select(m => new
                {
                    m.MaterialID,
                    m.MaterialCode,
                    m.MaterialName,
                    m.Category,
                    m.Unit,
                    m.CurrentStock,
                    m.MinimumStock,
                    m.ReorderLevel,
                    m.UnitCost,
                    m.SupplierInfo,
                    m.LastUpdated,
                    m.UpdatedBy,
                    StockStatus =
                        m.CurrentStock <= 0
                            ? "Out of Stock"
                            : m.CurrentStock <= m.ReorderLevel
                                ? "Low Stock"
                                : "In Stock"
                })
                .ToListAsync();

            var finishedGoods = await _context.FinishedGoods
                .AsNoTracking()
                .OrderBy(p => p.ProductName)
                .Select(p => new
                {
                    p.ProductID,
                    p.ProductCode,
                    p.ProductName,
                    p.Category,
                    p.Unit,
                    p.CurrentStock,
                    p.UnitPrice,
                    p.ProductionBatchID,
                    p.BatchID,
                    p.QualityStatus,
                    p.QualityInspectorID,
                    p.ProductionDate,
                    p.RecordedBy,
                    p.RecordedAt,
                    p.InspectionNotes
                })
                .ToListAsync();

            return Ok(new
            {
                success = true,
                rawMaterialCount = rawMaterials.Count,
                finishedGoodsCount = finishedGoods.Count,
                rawMaterials,
                finishedGoods
            });
        }

        // ============================================================
        // RAW MATERIALS
        // GET: /api/inventory/raw-materials
        // ============================================================

        [HttpGet("raw-materials")]
        public async Task<IActionResult> GetRawMaterials()
        {
            var materials = await _context.RawMaterials
                .AsNoTracking()
                .OrderBy(m => m.MaterialName)
                .Select(m => new
                {
                    m.MaterialID,
                    m.MaterialCode,
                    m.MaterialName,
                    m.Category,
                    m.Unit,
                    m.CurrentStock,
                    m.MinimumStock,
                    m.ReorderLevel,
                    m.UnitCost,
                    m.SupplierInfo,
                    m.LastUpdated,
                    m.UpdatedBy,
                    StockStatus =
                        m.CurrentStock <= 0
                            ? "Out of Stock"
                            : m.CurrentStock <= m.ReorderLevel
                                ? "Low Stock"
                                : "In Stock"
                })
                .ToListAsync();

            return Ok(new
            {
                success = true,
                count = materials.Count,
                materials
            });
        }

        // ============================================================
        // RAW MATERIAL DETAILS
        // GET: /api/inventory/raw-materials/{id}
        // ============================================================

        [HttpGet("raw-materials/{id:int}")]
        public async Task<IActionResult> GetRawMaterial(int id)
        {
            var material = await _context.RawMaterials
                .AsNoTracking()
                .Where(m => m.MaterialID == id)
                .Select(m => new
                {
                    m.MaterialID,
                    m.MaterialCode,
                    m.MaterialName,
                    m.Category,
                    m.Unit,
                    m.CurrentStock,
                    m.MinimumStock,
                    m.ReorderLevel,
                    m.UnitCost,
                    m.SupplierInfo,
                    m.LastUpdated,
                    m.UpdatedBy,
                    StockStatus =
                        m.CurrentStock <= 0
                            ? "Out of Stock"
                            : m.CurrentStock <= m.ReorderLevel
                                ? "Low Stock"
                                : "In Stock"
                })
                .FirstOrDefaultAsync();

            if (material == null)
            {
                return NotFound(new
                {
                    success = false,
                    message = "Raw material was not found."
                });
            }

            return Ok(new
            {
                success = true,
                material
            });
        }

        // ============================================================
        // FINISHED GOODS
        // GET: /api/inventory/finished-goods
        // ============================================================

        [HttpGet("finished-goods")]
        public async Task<IActionResult> GetFinishedGoods()
        {
            var products = await _context.FinishedGoods
                .AsNoTracking()
                .OrderBy(p => p.ProductName)
                .Select(p => new
                {
                    p.ProductID,
                    p.ProductCode,
                    p.ProductName,
                    p.Category,
                    p.Unit,
                    p.CurrentStock,
                    p.UnitPrice,
                    p.ProductionBatchID,
                    p.BatchID,
                    p.QualityStatus,
                    p.QualityInspectorID,
                    p.ProductionDate,
                    p.RecordedBy,
                    p.RecordedAt,
                    p.InspectionNotes
                })
                .ToListAsync();

            return Ok(new
            {
                success = true,
                count = products.Count,
                products
            });
        }

        // ============================================================
        // FINISHED GOOD DETAILS
        // GET: /api/inventory/finished-goods/{id}
        // ============================================================

        [HttpGet("finished-goods/{id:int}")]
        public async Task<IActionResult> GetFinishedGood(int id)
        {
            var product = await _context.FinishedGoods
                .AsNoTracking()
                .FirstOrDefaultAsync(p => p.ProductID == id);

            if (product == null)
            {
                return NotFound(new
                {
                    success = false,
                    message = "Finished good was not found."
                });
            }

            return Ok(new
            {
                success = true,
                product = new
                {
                    product.ProductID,
                    product.ProductCode,
                    product.ProductName,
                    product.Category,
                    product.Unit,
                    product.CurrentStock,
                    product.UnitPrice,
                    product.ProductionBatchID,
                    product.BatchID,
                    product.QualityStatus,
                    product.QualityInspectorID,
                    product.ProductionDate,
                    product.RecordedBy,
                    product.RecordedAt,
                    product.InspectionNotes
                }
            });
        }

        // ============================================================
        // LOW STOCK
        // GET: /api/inventory/low-stock
        // ============================================================

        [HttpGet("low-stock")]
        public async Task<IActionResult> GetLowStock()
        {
            var materials = await _context.RawMaterials
                .AsNoTracking()
                .Where(m => m.CurrentStock <= m.ReorderLevel)
                .OrderBy(m => m.CurrentStock)
                .ThenBy(m => m.MaterialName)
                .Select(m => new
                {
                    m.MaterialID,
                    m.MaterialCode,
                    m.MaterialName,
                    m.Category,
                    m.Unit,
                    m.CurrentStock,
                    m.MinimumStock,
                    m.ReorderLevel,
                    m.UnitCost,
                    m.SupplierInfo,
                    m.LastUpdated,
                    m.UpdatedBy,
                    ShortageQuantity =
                        m.CurrentStock < m.ReorderLevel
                            ? m.ReorderLevel - m.CurrentStock
                            : 0,
                    StockStatus =
                        m.CurrentStock <= 0
                            ? "Out of Stock"
                            : "Low Stock"
                })
                .ToListAsync();

            return Ok(new
            {
                success = true,
                count = materials.Count,
                materials
            });
        }

        // ============================================================
        // TRANSACTION HISTORY
        // GET: /api/inventory/transactions
        // Optional filters:
        // ?type=Receipt
        // ?materialId=1
        // ?productId=1
        // ============================================================

        [HttpGet("transactions")]
        public async Task<IActionResult> GetTransactions(
            [FromQuery] string? type = null,
            [FromQuery] int? materialId = null,
            [FromQuery] int? productId = null
        )
        {
            var query = _context.InventoryTransactions
                .AsNoTracking()
                .AsQueryable();

            if (!string.IsNullOrWhiteSpace(type))
            {
                query = query.Where(t => t.TransactionType == type);
            }

            if (materialId.HasValue)
            {
                query = query.Where(t => t.MaterialID == materialId.Value);
            }

            if (productId.HasValue)
            {
                query = query.Where(t => t.ProductID == productId.Value);
            }

            var transactions = await (
                from transaction in query
                join material in _context.RawMaterials.AsNoTracking()
                    on transaction.MaterialID equals (int?)material.MaterialID
                    into materialJoin
                from material in materialJoin.DefaultIfEmpty()

                join product in _context.FinishedGoods.AsNoTracking()
                    on transaction.ProductID equals (int?)product.ProductID
                    into productJoin
                from product in productJoin.DefaultIfEmpty()

                join employee in _context.Employees.AsNoTracking()
                    on transaction.PerformedBy equals (int?)employee.EmployeeID
                    into employeeJoin
                from employee in employeeJoin.DefaultIfEmpty()

                orderby transaction.TransactionDate descending,
                    transaction.TransactionID descending

                select new
                {
                    transaction.TransactionID,
                    transaction.MaterialID,
                    MaterialCode = material != null ? material.MaterialCode : null,
                    MaterialName = material != null ? material.MaterialName : null,

                    transaction.ProductID,
                    ProductCode = product != null ? product.ProductCode : null,
                    ProductName = product != null ? product.ProductName : null,

                    transaction.TransactionType,
                    transaction.Quantity,
                    transaction.UnitCost,
                    transaction.ReferenceNumber,
                    transaction.Notes,
                    transaction.TransactionDate,
                    transaction.PerformedBy,
                    PerformedByName =
                        employee != null
                            ? employee.FullName
                            : null,

                    ItemType =
                        transaction.MaterialID != null
                            ? "Raw Material"
                            : "Finished Good",

                    ItemCode =
                        material != null
                            ? material.MaterialCode
                            : product != null
                                ? product.ProductCode
                                : null,

                    ItemName =
                        material != null
                            ? material.MaterialName
                            : product != null
                                ? product.ProductName
                                : null
                }
            ).ToListAsync();

            return Ok(new
            {
                success = true,
                count = transactions.Count,
                transactions
            });
        }

        // ============================================================
        // RECORD STOCK MOVEMENT
        // POST: /api/inventory/transactions
        //
        // Transaction types supported by the existing database:
        // Receipt, Issue, Adjustment, Return
        // ============================================================

        [HttpPost("transactions")]
        public async Task<IActionResult> CreateTransaction(
            [FromBody] InventoryTransactionRequest request
        )
        {
            var validation = ValidateTransactionRequest(request);

            if (validation != null)
            {
                return BadRequest(new
                {
                    success = false,
                    message = validation
                });
            }

            var employee = await GetCurrentEmployeeAsync();

            if (employee == null)
            {
                return Unauthorized(new
                {
                    success = false,
                    message = "The logged-in employee could not be verified."
                });
            }

            await using var databaseTransaction =
                await _context.Database.BeginTransactionAsync();

            try
            {
                decimal? unitCost = request.UnitCost;

                if (request.MaterialID.HasValue)
                {
                    var material = await _context.RawMaterials
                        .FirstOrDefaultAsync(
                            m => m.MaterialID == request.MaterialID.Value
                        );

                    if (material == null)
                    {
                        return NotFound(new
                        {
                            success = false,
                            message = "Raw material was not found."
                        });
                    }

                    var newStock = CalculateNewStock(
                        currentStock: material.CurrentStock,
                        quantity: request.Quantity,
                        transactionType: request.TransactionType
                    );

                    if (newStock < 0)
                    {
                        return BadRequest(new
                        {
                            success = false,
                            message =
                                $"Insufficient stock. {material.MaterialName} currently has " +
                                $"{material.CurrentStock} {material.Unit} available."
                        });
                    }

                    material.CurrentStock = newStock;
                    material.LastUpdated = DateTime.UtcNow;
                    material.UpdatedBy = employee.EmployeeID;

                    if (
                        request.UnitCost.HasValue &&
                        request.UnitCost.Value >= 0 &&
                        (
                            request.TransactionType.Equals(
                                "Receipt",
                                StringComparison.OrdinalIgnoreCase
                            ) ||
                            request.TransactionType.Equals(
                                "Adjustment",
                                StringComparison.OrdinalIgnoreCase
                            )
                        )
                    )
                    {
                        material.UnitCost = request.UnitCost.Value;
                    }

                    unitCost ??= material.UnitCost;
                }
                else
                {
                    var product = await _context.FinishedGoods
                        .FirstOrDefaultAsync(
                            p => p.ProductID == request.ProductID!.Value
                        );

                    if (product == null)
                    {
                        return NotFound(new
                        {
                            success = false,
                            message = "Finished good was not found."
                        });
                    }

                    var newStock = CalculateNewStock(
                        currentStock: product.CurrentStock,
                        quantity: request.Quantity,
                        transactionType: request.TransactionType
                    );

                    if (newStock < 0)
                    {
                        return BadRequest(new
                        {
                            success = false,
                            message =
                                $"Insufficient stock. {product.ProductName} currently has " +
                                $"{product.CurrentStock} {product.Unit} available."
                        });
                    }

                    product.CurrentStock = newStock;

                    // InventoryTransaction contains UnitCost, while
                    // FinishedGood contains UnitPrice. We do not overwrite
                    // the product's selling price from an inventory movement.
                    unitCost ??= null;
                }

                var transaction = new InventoryTransaction
                {
                    MaterialID = request.MaterialID,
                    ProductID = request.ProductID,
                    TransactionType = NormalizeTransactionType(
                        request.TransactionType
                    ),
                    Quantity = request.Quantity,
                    UnitCost = unitCost,
                    ReferenceNumber =
                        CleanOptionalText(request.ReferenceNumber),
                    Notes =
                        CleanOptionalText(request.Notes),
                    TransactionDate = DateTime.UtcNow,
                    PerformedBy = employee.EmployeeID
                };

                _context.InventoryTransactions.Add(transaction);

                await _context.SaveChangesAsync();
                await databaseTransaction.CommitAsync();

                return Ok(new
                {
                    success = true,
                    message = "Inventory transaction recorded successfully.",
                    transaction = new
                    {
                        transaction.TransactionID,
                        transaction.MaterialID,
                        transaction.ProductID,
                        transaction.TransactionType,
                        transaction.Quantity,
                        transaction.UnitCost,
                        transaction.ReferenceNumber,
                        transaction.Notes,
                        transaction.TransactionDate,
                        transaction.PerformedBy,
                        performedByName = employee.FullName
                    }
                });
            }
            catch
            {
                await databaseTransaction.RollbackAsync();
                throw;
            }
        }

        // ============================================================
        // PRIVATE HELPERS
        // ============================================================

        private async Task<Employee?> GetCurrentEmployeeAsync()
        {
            var employeeIdValue =
                User.FindFirstValue(ClaimTypes.NameIdentifier);

            if (
                !int.TryParse(
                    employeeIdValue,
                    out var employeeId
                )
            )
            {
                return null;
            }

            return await _context.Employees
                .FirstOrDefaultAsync(
                    e =>
                        e.EmployeeID == employeeId &&
                        e.EmployeeStatus == "Active" &&
                        (
                            e.Role == "InventoryClerk" ||
                            e.Role == "Admin"
                        )
                );
        }

        private static string? ValidateTransactionRequest(
            InventoryTransactionRequest request
        )
        {
            if (
                request.MaterialID.HasValue ==
                request.ProductID.HasValue
            )
            {
                return
                    "Select exactly one inventory item: either a raw material or a finished good.";
            }

            if (request.Quantity <= 0)
            {
                return "Quantity must be greater than zero.";
            }

            if (
                request.UnitCost.HasValue &&
                request.UnitCost.Value < 0
            )
            {
                return "Unit cost cannot be negative.";
            }

            var transactionType =
                NormalizeTransactionType(
                    request.TransactionType
                );

            if (
                transactionType != "Receipt" &&
                transactionType != "Issue" &&
                transactionType != "Adjustment" &&
                transactionType != "Return"
            )
            {
                return
                    "Transaction type must be Receipt, Issue, Adjustment, or Return.";
            }

            return null;
        }

        private static decimal CalculateNewStock(
            decimal currentStock,
            decimal quantity,
            string transactionType
        )
        {
            var normalizedType =
                NormalizeTransactionType(transactionType);

            return normalizedType switch
            {
                "Receipt" => currentStock + quantity,
                "Return" => currentStock + quantity,
                "Issue" => currentStock - quantity,

                // For this MVP, Adjustment represents a positive
                // stock correction. Negative corrections should be
                // recorded as an Issue so Quantity remains positive.
                "Adjustment" => currentStock + quantity,

                _ => currentStock
            };
        }

        private static string NormalizeTransactionType(
            string? transactionType
        )
        {
            if (string.IsNullOrWhiteSpace(transactionType))
            {
                return string.Empty;
            }

            return transactionType.Trim().ToLowerInvariant() switch
            {
                "receipt" => "Receipt",
                "issue" => "Issue",
                "adjustment" => "Adjustment",
                "return" => "Return",
                _ => transactionType.Trim()
            };
        }

        private static string? CleanOptionalText(string? value)
        {
            return string.IsNullOrWhiteSpace(value)
                ? null
                : value.Trim();
        }
    }

    // ================================================================
    // REQUEST DTO
    // ================================================================

    public class InventoryTransactionRequest
    {
        public int? MaterialID { get; set; }

        public int? ProductID { get; set; }

        public string TransactionType { get; set; } = string.Empty;

        public decimal Quantity { get; set; }

        public decimal? UnitCost { get; set; }

        public string? ReferenceNumber { get; set; }

        public string? Notes { get; set; }
    }
}
