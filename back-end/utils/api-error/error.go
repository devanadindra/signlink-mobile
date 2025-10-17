package apierror

import (
	"net/http"
)

func Unauthorized() error {
	return NewWarn(http.StatusUnauthorized, "Unauthorized!")
}

func FailedToConvertUpdatedAt() error {
	return NewError(http.StatusInternalServerError, "Failed convert updated at to time")
}

func FailedToConvertCreatedAt() error {
	return NewError(http.StatusInternalServerError, "Failed convert created at to time")
}

func CustomerNotFound() error {
	return NewWarn(http.StatusNotFound, "Customer not found!")
}

func InvalidCustomerId() error {
	return NewWarn(http.StatusBadRequest, "customerId must be UUID!")
}

func AddressNotFound() error {
	return NewWarn(http.StatusNotFound, "Address not found!")
}

func InvalidAddressId() error {
	return NewWarn(http.StatusBadRequest, "addressId must be UUID!")
}

func ProductNotFound() error {
	return NewWarn(http.StatusNotFound, "Product not found!")
}

func InvalidProductId() error {
	return NewWarn(http.StatusBadRequest, "productId must be UUID!")
}

func ProductOtherNameNotFound() error {
	return NewWarn(http.StatusNotFound, "Product other name not found!")
}

func ProductVariantNotFound() error {
	return NewWarn(http.StatusNotFound, "Product variant not found!")
}

func ProductImageNotFound() error {
	return NewWarn(http.StatusNotFound, "Product image not found!")
}

func InvalidProductOtherNameId() error {
	return NewWarn(http.StatusBadRequest, "productOtherNameId must be UUID!")
}

func InvalidProductVariantId() error {
	return NewWarn(http.StatusBadRequest, "productVariantId must be UUID!")
}

func InvalidProductImageId() error {
	return NewWarn(http.StatusBadRequest, "productImageId must be UUID!")
}

func ProductHistoryNotFound() error {
	return NewWarn(http.StatusNotFound, "Product history not found!")
}

func TransactionNotFound() error {
	return NewWarn(http.StatusNotFound, "Transaction not found!")
}

func InvalidOrderId() error {
	return NewWarn(http.StatusBadRequest, "orderId must be UUID!")
}

func ExpenseNotFound() error {
	return NewWarn(http.StatusNotFound, "Expense not found!")
}

func InvalidExpenseId() error {
	return NewWarn(http.StatusBadRequest, "expenseId must be UUID!")
}

func InvalidExpenseEvidenceId() error {
	return NewWarn(http.StatusBadRequest, "expenseEvidenceId must be UUID!")
}

func ExpenseEvidenceNotFound() error {
	return NewWarn(http.StatusNotFound, "Expense evidence not found!")
}

func FileNotFound() error {
	return NewWarn(http.StatusNotFound, "File not found!")
}

func InvalidFileId() error {
	return NewWarn(http.StatusBadRequest, "fileId must be UUID!")
}
